package com.catnip.cowboyshoot.ui.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.catnip.cowboyshoot.R
import com.catnip.cowboyshoot.databinding.ActivityMenuGameBinding
import com.catnip.cowboyshoot.enum.GameState
import com.catnip.cowboyshoot.ui.game.GameActivity

class MenuGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuGameBinding

    private val name: String? by lazy {
        intent.getStringExtra(EXTRAS_NAME)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setNameOnTitle()
        setMenuClickListeners()
    }

    private fun setMenuClickListeners() {
        binding.ivMenuVsComputer.setOnClickListener {
            GameActivity.startActivity(this,false)
        }
        binding.ivMenuVsPlayer.setOnClickListener {
            GameActivity.startActivity(this,true)
        }
    }

    private fun setNameOnTitle() {
        binding.tvTitleMenu.text = getString(R.string.placeholder_title_menu_game, name)
    }


    companion object {
        private const val EXTRAS_NAME = "EXTRAS_NAME"

        fun startActivity(context: Context, name: String) {
            context.startActivity(Intent(context,MenuGameActivity::class.java).apply {
                putExtra(EXTRAS_NAME,name)
            })
        }
    }
}