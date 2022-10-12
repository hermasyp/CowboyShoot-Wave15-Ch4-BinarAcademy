package com.catnip.cowboyshoot.ui.game

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.catnip.cowboyshoot.R
import com.catnip.cowboyshoot.databinding.ActivityGameBinding
import com.catnip.cowboyshoot.enum.GameState
import com.catnip.cowboyshoot.enum.PlayerPosition
import com.catnip.cowboyshoot.enum.PlayerSide
import com.catnip.cowboyshoot.manager.CowboyGameListener
import com.catnip.cowboyshoot.manager.CowboyGameManager
import com.catnip.cowboyshoot.manager.CowboyGameManagerImpl
import com.catnip.cowboyshoot.manager.MultiplayerCowboyGameManager
import com.catnip.cowboyshoot.model.Player

class GameActivity : AppCompatActivity(), CowboyGameListener {
    private val binding: ActivityGameBinding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    private val isUsingMultiplayerMode: Boolean by lazy {
        intent.getBooleanExtra(EXTRAS_MULTIPLAYER_MODE, false)
    }

    private val cowboyGameManager: CowboyGameManager by lazy {
        if (isUsingMultiplayerMode)
            MultiplayerCowboyGameManager(this)
        else
            CowboyGameManagerImpl(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        cowboyGameManager.initGame()
        setOnClickListeners()
        supportActionBar?.hide()
    }

    private fun setOnClickListeners() {
        binding.ivArrowUp.setOnClickListener {
            cowboyGameManager.movePlayerToTop()
        }
        binding.ivArrowDown.setOnClickListener {
            cowboyGameManager.movePlayerToBottom()
        }
        binding.tvActionGame.setOnClickListener {
            cowboyGameManager.startOrRestartGame()
        }
    }

    override fun onPlayerStatusChanged(player: Player, iconDrawableRes: Int) {
        setCharacterMovement(player, iconDrawableRes)
    }

    override fun onGameStateChanged(gameState: GameState) {
        binding.tvStatusGame.text = ""
        when (gameState) {
            GameState.IDLE -> {
                binding.tvActionGame.text = getString(R.string.text_fire)
                setCharacterVisibility(isPlayerOneVisible = true, isPlayerTwoVisible = true)
            }
            GameState.STARTED -> {
                binding.tvActionGame.text = getString(R.string.text_fire)
                setCharacterVisibility(isPlayerOneVisible = true, isPlayerTwoVisible = true)
            }
            GameState.FINISHED -> {
                binding.tvActionGame.text = getString(R.string.text_restart)
                setCharacterVisibility(isPlayerOneVisible = true, isPlayerTwoVisible = true)
            }
            GameState.PLAYER_ONE_TURN -> {
                binding.tvActionGame.text = getString(R.string.text_lock_player_one)
                setCharacterVisibility(isPlayerOneVisible = true, isPlayerTwoVisible = false)
            }
            GameState.PLAYER_TWO_TURN -> {
                binding.tvActionGame.text = getString(R.string.text_fire)
                setCharacterVisibility(isPlayerOneVisible = false, isPlayerTwoVisible = true)
            }
        }

    }

    private fun setCharacterVisibility(isPlayerOneVisible: Boolean, isPlayerTwoVisible: Boolean) {
        binding.llPlayerLeft.isVisible = isPlayerOneVisible
        binding.llPlayerRight.isVisible = isPlayerTwoVisible
    }

    override fun onGameFinished(gameState: GameState, winner: Player) {
        if (winner.playerSide == PlayerSide.PLAYER_ONE) {
            binding.tvStatusGame.text = getString(R.string.text_you_win)
        } else {
            binding.tvStatusGame.text = getString(R.string.text_you_lose)
        }
    }

    private fun setCharacterMovement(player: Player, iconDrawableRes: Int) {
        val ivCharTop: ImageView?
        val ivCharMiddle: ImageView?
        val ivCharBottom: ImageView?
        val drawable = ContextCompat.getDrawable(this, iconDrawableRes)

        if (player.playerSide == PlayerSide.PLAYER_ONE) {
            ivCharTop = binding.ivPlayerLeftTop
            ivCharMiddle = binding.ivPlayerLeftMid
            ivCharBottom = binding.ivPlayerLeftBottom
        } else {
            ivCharTop = binding.ivPlayerRightTop
            ivCharMiddle = binding.ivPlayerRightMid
            ivCharBottom = binding.ivPlayerRightBottom
        }

        when (player.playerPosition) {
            PlayerPosition.TOP -> {
                ivCharTop.visibility = View.VISIBLE
                ivCharMiddle.visibility = View.INVISIBLE
                ivCharBottom.visibility = View.INVISIBLE
                ivCharTop.setImageDrawable(drawable)
            }
            PlayerPosition.MIDDLE -> {
                ivCharTop.visibility = View.INVISIBLE
                ivCharMiddle.visibility = View.VISIBLE
                ivCharBottom.visibility = View.INVISIBLE
                ivCharMiddle.setImageDrawable(drawable)

            }
            PlayerPosition.BOTTOM -> {
                ivCharTop.visibility = View.INVISIBLE
                ivCharMiddle.visibility = View.INVISIBLE
                ivCharBottom.visibility = View.VISIBLE
                ivCharBottom.setImageDrawable(drawable)
            }
        }
    }

    companion object {
        private const val EXTRAS_MULTIPLAYER_MODE = "EXTRAS_MULTIPLAYER_MODE"

        fun startActivity(context: Context, isUsingMultiplayerMode: Boolean) {
            context.startActivity(Intent(context, GameActivity::class.java).apply {
                putExtra(EXTRAS_MULTIPLAYER_MODE, isUsingMultiplayerMode)
            })
        }
    }
}