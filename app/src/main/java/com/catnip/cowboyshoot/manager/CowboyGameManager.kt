package com.catnip.cowboyshoot.manager

import com.catnip.cowboyshoot.R
import com.catnip.cowboyshoot.enum.GameState
import com.catnip.cowboyshoot.enum.PlayerPosition
import com.catnip.cowboyshoot.enum.PlayerSide
import com.catnip.cowboyshoot.enum.PlayerState
import com.catnip.cowboyshoot.model.Player
import kotlin.random.Random

/**
Written with love by Muhammad Hermas Yuda Pamungkas
Github : https://github.com/hermasyp
 **/
interface CowboyGameManager {
    fun initGame()
    fun movePlayerToTop()
    fun movePlayerToBottom()
    fun startOrRestartGame()
}

interface CowboyGameListener {
    fun onPlayerStatusChanged(player: Player, iconDrawableRes: Int)
    fun onGameStateChanged(gameState: GameState)
    fun onGameFinished(gameState: GameState, winner: Player)
}

open class CowboyGameManagerImpl(
    private val listener: CowboyGameListener
) : CowboyGameManager {

    protected lateinit var playerOne: Player

    protected lateinit var playerTwo: Player

    protected lateinit var state: GameState

    override fun initGame() {
        setGameState(GameState.IDLE)
        playerOne = Player(PlayerSide.PLAYER_ONE, PlayerState.IDLE, PlayerPosition.MIDDLE)
        playerTwo = Player(PlayerSide.PLAYER_TWO, PlayerState.IDLE, PlayerPosition.MIDDLE)
        notifyPlayerDataChanged()
        setGameState(GameState.STARTED)
    }

    private fun notifyPlayerDataChanged() {
        listener.onPlayerStatusChanged(
            playerOne,
            getPlayerOneDrawableByState(playerOne.playerState)
        )
        listener.onPlayerStatusChanged(
            playerTwo,
            getPlayerTwoDrawableByState(playerTwo.playerState)
        )
    }

    override fun movePlayerToTop() {
        if (state != GameState.FINISHED &&
            playerOne.playerPosition.ordinal > PlayerPosition.TOP.ordinal
        ) {
            val currentIndex = playerOne.playerPosition.ordinal
            setPlayerOneMovement(getPlayerPositionByOrdinal(currentIndex - 1), PlayerState.IDLE)
        }
    }

    override fun movePlayerToBottom() {
        if (state != GameState.FINISHED &&
            playerOne.playerPosition.ordinal < PlayerPosition.BOTTOM.ordinal
        ) {
            val currentIndex = playerOne.playerPosition.ordinal
            setPlayerOneMovement(getPlayerPositionByOrdinal(currentIndex + 1), PlayerState.IDLE)
        }
    }


    private fun setPlayerOneMovement(
        playerPosition: PlayerPosition = playerOne.playerPosition,
        playerState: PlayerState = playerOne.playerState
    ) {
        playerOne.apply {
            this.playerPosition = playerPosition
            this.playerState = playerState
        }
        listener.onPlayerStatusChanged(
            playerOne,
            getPlayerOneDrawableByState(playerOne.playerState)
        )
    }

    protected fun setPlayerTwoMovement(
        playerPosition: PlayerPosition = playerTwo.playerPosition,
        playerState: PlayerState = playerTwo.playerState
    ) {
        playerTwo.apply {
            this.playerPosition = playerPosition
            this.playerState = playerState
        }
        listener.onPlayerStatusChanged(
            playerTwo,
            getPlayerTwoDrawableByState(playerTwo.playerState)
        )
    }

    private fun getPlayerOneDrawableByState(playerState: PlayerState): Int {
        return when (playerState) {
            PlayerState.IDLE -> R.drawable.ic_cowboy_left_shoot_false
            PlayerState.SHOOT -> R.drawable.ic_cowboy_left_shoot_true
            PlayerState.DEAD -> R.drawable.ic_cowboy_left_dead
        }
    }

    private fun getPlayerTwoDrawableByState(playerState: PlayerState): Int {
        return when (playerState) {
            PlayerState.IDLE -> R.drawable.ic_cowboy_right_shoot_false
            PlayerState.SHOOT -> R.drawable.ic_cowboy_right_shoot_true
            PlayerState.DEAD -> R.drawable.ic_cowboy_right_dead
        }
    }

    protected fun getPlayerPositionByOrdinal(index: Int): PlayerPosition {
        return PlayerPosition.values()[index]
    }

    protected fun setGameState(newGameState: GameState) {
        state = newGameState
        listener.onGameStateChanged(state)
    }

    protected fun startGame() {
        playerTwo.apply {
            playerPosition = getPlayerTwoPosition()
        }
        checkPlayerWinner()
    }

    private fun checkPlayerWinner() {
        val winner = if (playerOne.playerPosition == playerTwo.playerPosition) {
            setPlayerOneMovement(playerState = PlayerState.DEAD)
            setPlayerTwoMovement(playerState = PlayerState.SHOOT)
            playerOne
        } else {
            setPlayerOneMovement(playerState = PlayerState.SHOOT)
            setPlayerTwoMovement(playerState = PlayerState.DEAD)
            playerTwo
        }
        setGameState(GameState.FINISHED)
        listener.onGameFinished(state, winner)
    }

    protected fun resetGame() {
        initGame()
    }

    protected open fun getPlayerTwoPosition(): PlayerPosition {
        val randomPosition = Random.nextInt(0, until = PlayerPosition.values().size)
        return getPlayerPositionByOrdinal(randomPosition)
    }

    override fun startOrRestartGame() {
        if (state != GameState.FINISHED) {
            startGame()
        } else {
            resetGame()
        }
    }
}


class MultiplayerCowboyGameManager(listener: CowboyGameListener) : CowboyGameManagerImpl(listener) {

    override fun initGame() {
        super.initGame()
        setGameState(GameState.PLAYER_ONE_TURN)
    }

    override fun getPlayerTwoPosition(): PlayerPosition {
        return playerTwo.playerPosition
    }

    override fun movePlayerToTop() {
        if (state == GameState.PLAYER_ONE_TURN) {
            super.movePlayerToTop()
        } else if (state == GameState.PLAYER_TWO_TURN) {
            if (playerTwo.playerPosition.ordinal > PlayerPosition.TOP.ordinal) {
                val currentIndex = playerTwo.playerPosition.ordinal
                setPlayerTwoMovement(getPlayerPositionByOrdinal(currentIndex - 1), PlayerState.IDLE)
            }
        }
    }

    override fun movePlayerToBottom() {
        if (state == GameState.PLAYER_ONE_TURN) {
            super.movePlayerToBottom()
        } else if (state == GameState.PLAYER_TWO_TURN) {
            if (playerTwo.playerPosition.ordinal < PlayerPosition.BOTTOM.ordinal) {
                val currentIndex = playerTwo.playerPosition.ordinal
                setPlayerTwoMovement(getPlayerPositionByOrdinal(currentIndex + 1), PlayerState.IDLE)
            }
        }
    }

    override fun startOrRestartGame() {
        when (state) {
            GameState.PLAYER_ONE_TURN -> {
                setGameState(GameState.PLAYER_TWO_TURN)
            }
            GameState.PLAYER_TWO_TURN -> {
                startGame()
            }
            GameState.FINISHED -> {
                resetGame()
            }
            else -> return
        }
    }

}

