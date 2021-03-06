package io.indexpath.todo

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.TextView



class Splash : AppCompatActivity() {

    lateinit var splashTread : Thread
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val window = getWindow()
        window.setFormat(PixelFormat.RGBA_8888)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        var ab = getSupportActionBar()
//        ab!!.hide()
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash)
        StartAnimations()
    }

    private fun StartAnimations() {
        var anim = AnimationUtils.loadAnimation(this, R.anim.alpha)
        anim.reset()
        val l = findViewById<ConstraintLayout>(R.id.const_layout)
        l.clearAnimation()
        l.startAnimation(anim)

        anim = AnimationUtils.loadAnimation(this, R.anim.translate)
        anim.reset()
        val iv = findViewById<TextView>(R.id.splash)
        iv.clearAnimation()
        iv.startAnimation(anim)


        anim = AnimationUtils.loadAnimation(this, R.anim.alphadown)
        splashTread = object:Thread() {
            override fun run() {
                try
                {
                    var waited = 0
                    // Splash screen pause time
                    while (waited < 3500)
                    {
                        Thread.sleep(100)
                        waited += 100
                    }


                    //anim.reset()
                    val iv = findViewById<TextView>(R.id.splash)
                    iv.clearAnimation()
                    iv.startAnimation(anim)

                    val intent = Intent(this@Splash,
                            LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    this@Splash.finish()
                }
                catch (e:InterruptedException) {
                    // do nothing
                }
                finally
                {
                    this@Splash.finish()
                }
            }
        }
        splashTread.start()
    }

}
