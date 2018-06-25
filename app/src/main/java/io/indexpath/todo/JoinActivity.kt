package io.indexpath.todo

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxTextView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import es.dmoral.toasty.Toasty
import io.indexpath.todo.realmDB.Person
import io.indexpath.todo.realmDB.UserRealmManager
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_join.*
import kotlinx.android.synthetic.main.custom_bar_main.view.*
import org.jetbrains.anko.startActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class JoinActivity : AppCompatActivity() {

    var userRealmManager = UserRealmManager()
    var imageIdList = arrayOf<Int>(
            R.drawable.random_image_01, R.drawable.random_image_02, R.drawable.random_image_03, R.drawable.random_image_04, R.drawable.random_image_05, R.drawable.random_image_06)

    private val GALLERY = 1
    private val CAMERA = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.custom_bar_addtodo)
        supportActionBar!!.customView.customTitle.setText("Create an Account")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        /** 기본 프로필 이미지, 최초 디폴트 6개 이미지 중에서 랜덤으로 선택 */
        val num = Random().nextInt(imageIdList.size)
        profile_pic.setImageResource(imageIdList[num])
        Log.d(TAG, "이미지 갯수 : ${imageIdList.size} 넘 : $num")

        /** 가입 버튼 관련 옵저버
         * 오류메세지와는 관계없이 단순히 패턴검사만 하여 가입 버튼 활성화에 사용*/
        val observableId = RxTextView.textChanges(this!!.todoInputText)
                .map { t -> CustomPatterns.idPattern.matcher(t).matches() }

        val observableDoubleId = RxTextView.textChanges(todoInputText)
                .map { t -> checkDoubleIdForButton(t) }

        val observableEmail = RxTextView.textChanges(editTextEmail)
                .map { t -> Patterns.EMAIL_ADDRESS.matcher(t).matches() }

        val observablePw1 = RxTextView.textChanges(editTextPassword)
                .map { t -> CustomPatterns.pwPattern.matcher(t).matches() }

        val observablePw2 = RxTextView.textChanges(editTextPasswordAgain)
                .map { t -> CustomPatterns.passwordTemp.equals(t.toString()) }

        /** 패턴 오류 메세지 보여줌 오류내용은 CustomPatterns 에서 가져옴 */
        /** 아이디 체크 */
        RxTextView.afterTextChangeEvents(todoInputText)
                .skipInitialValue()
                .map {
                    checkDoubleIdText.text = ""
                    it.view().text.toString()
                }
                // 1초마다 새롭게 시도
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(CustomPatterns.checkIdPattern)
                .compose(CustomPatterns.doubleId) /** <--- 중복아이디 검사 */
                .compose(retryWhenError {
                    checkDoubleIdText.text = it.message
                })
                .subscribe()

        /** 이메일 체크 */
        RxTextView.afterTextChangeEvents(editTextEmail)
                .skipInitialValue()
                .map {
                    checkId.text = ""
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(CustomPatterns.checkEmailPattern)
                .compose(retryWhenError {
                    checkId.text = it.message
                })
                .subscribe()

        /** 첫번째 패스워드 체크 */
        RxTextView.afterTextChangeEvents(editTextPassword)
                .skipInitialValue()
                .map {
                    checkId.text = ""
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(CustomPatterns.checkPwPattern)
                .compose(retryWhenError {
                    checkId.text = it.message
                })
                .subscribe {
                    CustomPatterns.passwordTemp = it
                    editTextPasswordAgain.text = null
                }


        /** 동일한 패스워드인지 체크 */
        RxTextView.afterTextChangeEvents(editTextPasswordAgain)
                .skipInitialValue()
                .map {
                    checkId.text = ""
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(CustomPatterns.comparePw)
                .compose(retryWhenError {
                    checkId.text = it.message
                })
                .subscribe()


        /** 버튼 활성화 이상형 월드컵
         * 모든 항목이 트루를 받아야만 버튼이 활성화 됨 */
        val signInEnabled1: Observable<Boolean> = Observable.combineLatest(
                observableId, observableEmail, BiFunction { i, e -> i && e }
        )

        val signInEnabled2: Observable<Boolean> = Observable.combineLatest(
                observablePw1, observablePw2, BiFunction { p1, p2 -> p1 && p2 }
        )

        val signInEnabled3: Observable<Boolean> = Observable.combineLatest(
                signInEnabled1, signInEnabled2, BiFunction { s1, s2 -> s1 && s2 }
        )

        val signInEnabled: Observable<Boolean> = Observable.combineLatest(
                signInEnabled3, observableDoubleId, BiFunction { s1, s2 -> s1 && s2}
        )

        // 버튼 활성하면서 색상도 변경
        signInEnabled.distinctUntilChanged()
                .subscribe { enabled -> buttonLogOut.isEnabled = enabled }
        signInEnabled.distinctUntilChanged()
                .map { b -> if (b) R.color.buttonOn else R.color.buttonOff }
                .subscribe { color -> buttonLogOut.backgroundTintList =
                        ContextCompat.getColorStateList(this, color) }

        buttonLogOut.setOnClickListener {

            /** 렘에 회원정보 저장 */
            val bitmap = (profile_pic!!.drawable as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val profileImage = stream.toByteArray()

            val person = Person()
            person.userId = todoInputText.text.toString()
            person.email = editTextEmail.text.toString()
            person.password = editTextPassword.text.toString()
            person.profilePic = profileImage
            userRealmManager.insertUser(Person::class.java, person)

            Toasty.success(this, "저장 성공", Toast.LENGTH_SHORT, true).show()

            startActivity<LoginActivity>()
            finish()
            // to previous
            overridePendingTransition(R.anim.activity_slide_enter, R.anim.activity_slide_exit)
        }

        /** 프로파일 이미지 변경 클릭 */
        changeProfileButton.setOnClickListener {
            showPictureDialog()
        }
    }

    /** 이미지 소스 선택 */
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> requestCameraPermission()
            }
        }
        pictureDialog.show()
    }

    /** 카메라 퍼미션 획득 */
    fun requestCameraPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object: PermissionListener {
                    override fun onPermissionGranted(
                            response: PermissionGrantedResponse?) {
                        takePhotoFromCamera()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {

                        token?.continuePermissionRequest()
                        /** 다이얼로그 단계가 불필요하여 주석처리 */
//                        AlertDialog.Builder(this@JoinActivity)
//                                .setTitle("타이틀")
//                                .setMessage("메세지")
//                                .setNegativeButton(android.R.string.cancel, DialogInterface.OnClickListener
//                                        { dialogInterface, i ->
//                                            dialogInterface.dismiss()
//                                            token?.cancelPermissionRequest()
//                                        })
//                                .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener
//                                        { dialogInterface, i ->
//                                            dialogInterface.dismiss()
//                                            token?.continuePermissionRequest()
//                                        })
//                                .setOnDismissListener({
//                                    token?.cancelPermissionRequest() })
//                                .show()
                    }

                    override fun onPermissionDenied( response: PermissionDeniedResponse?) {
                        Toasty.error(this@JoinActivity, "카메라 사용 허용", Toast.LENGTH_SHORT, true).show()
                    }
                })
                .check()
    }

    /** 갤러리에서 이미지 선택 */
    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
    }

    /** 카메라 이용 이미지 선택*/
    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    /** 사용할 이미지 선택한 후 프로필 이미지에 셋트 */
    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    profile_pic.setImageBitmap(bitmap)
                }
                catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        else if (requestCode == CAMERA)
        {
            if (data != null)
            {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                profile_pic.setImageBitmap(thumbnail)
                saveImage(thumbnail)
            }
        }
    }

    /** 카메라 이용해서 프로필 이미지 사용시 앨범에도 사진 저장 */
    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
                (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {
            wallpaperDirectory.mkdirs()
        }

        try
        {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance().getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this, arrayOf(f.getPath()), arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("gfd", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    /** 버튼 활성화 관련 옵저버 observableDoubleId 에 불린 값 전달 */
    private fun checkDoubleIdForButton(t: CharSequence?) : Boolean {
        val config = RealmConfiguration.Builder().name("person.realm").build()
        val realm = Realm.getInstance(config)
        var count = realm.where(Person::class.java).equalTo("userId", t.toString()).findAll().count()
        // 중복 아이디 없으면 트루 전달
        if (count == 0) { return true } else { return false }
    }


    /** 패턴들 오류 메세지 관련 */
    private inline fun retryWhenError(crossinline onError: (ex: Throwable) -> Unit): ObservableTransformer<String, String> = ObservableTransformer { observable ->
        observable.retryWhen { errors ->
            errors.flatMap {
                onError(it)
                Observable.just("")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.activity_slide_enter, R.anim.activity_slide_exit)
    }

    companion object {
        private val TAG = "Todo"
        private val IMAGE_DIRECTORY = "/Todos"
    }
}



