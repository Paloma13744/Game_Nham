package app.com.gamenhamnham

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.forEach

import app.com.gamenhamnham.databinding.ActivityJogoBinding

class JogoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJogoBinding
    private var currentPlayer = 1 // 1 para jogador 1, 2 para jogador 2
    private val board = Array(3) { Array(3) { mutableListOf<Pair<Int, Int>>() } } // Cada célula contém uma lista de pares (tamanho da peça, jogador)
    private var selectedPiece: ImageView? = null  // Variável para armazenar a peça selecionada
    private val player1Pieces = intArrayOf(3, 3, 3) // [pequena, média, grande]
    private val player2Pieces = intArrayOf(3, 3, 3) // [pequena, média, grande]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJogoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()  // Configura os listeners de clique
        updateTurnIndicator()  // Atualiza o indicador de turno

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupClickListeners() {
        // Configurar os cliques nas peças do jogador 1
        binding.jog1PiecesLayout.forEach { piece ->
            piece.setOnClickListener {
                if (currentPlayer == 1 && hasAvailablePieces(1, piece)) {
                    selectPiece(it as ImageView)
                } else {
                    Toast.makeText(this, "Não é sua vez ou você não tem mais peças deste tamanho!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configurar os cliques nas peças do jogador 2
        binding.jog2PiecesLayout.forEach { piece ->
            piece.setOnClickListener {
                if (currentPlayer == 2 && hasAvailablePieces(2, piece)) {
                    selectPiece(it as ImageView)
                } else {
                    Toast.makeText(this, "Não é sua vez ou você não tem mais peças deste tamanho!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configurar os cliques nas células do tabuleiro
        for (row in 0..2) {
            for (col in 0..2) {
                val cellId = "cell_${row}${col}"
                val cell = binding.root.findViewById<ImageView>(resources.getIdentifier(cellId, "id", packageName))
                cell.setOnClickListener {
                    if (selectedPiece != null) {
                        handlePieceDrop(cell, selectedPiece!!, row, col)
                    } else {
                        Toast.makeText(this, "Selecione uma peça primeiro!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun selectPiece(piece: ImageView) {
        selectedPiece?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent)) // Desmarca a peça anterior
        selectedPiece = piece // Marca a nova peça como selecionada
        piece.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_200)) // Destaca a peça selecionada
    }

    private fun handlePieceDrop(cell: ImageView, piece: ImageView, row: Int, col: Int) {
        val pieceSize = when (piece.contentDescription.toString()) {
            "Peça pequena jogador 1", "Peça pequena jogador 2" -> 0
            "Peça média jogador 1", "Peça média jogador 2" -> 1
            "Peça grande jogador 1", "Peça grande jogador 2" -> 2
            else -> -1
        }

        if (pieceSize == -1) {
            Toast.makeText(this, "Tamanho de peça desconhecido!", Toast.LENGTH_SHORT).show()
            return
        }

        if (board[row][col].isNotEmpty() && board[row][col].last().first >= pieceSize) {
            Toast.makeText(this, "Você não pode colocar uma peça menor aqui!", Toast.LENGTH_SHORT).show()
            return
        }

        board[row][col].add(Pair(pieceSize, currentPlayer)) // Adiciona a peça ao tabuleiro com seu jogador associado
        cell.setImageDrawable(piece.drawable)  // Coloca a peça na célula do tabuleiro

        // Atualiza a quantidade de peças restantes
        if (currentPlayer == 1) {
            player1Pieces[pieceSize]--
            // Esconde a peça quando não houver mais peças daquele tamanho
            if (player1Pieces[pieceSize] == 0) {
                hidePiece(piece, binding.jog1PiecesLayout)
            }
        } else {
            player2Pieces[pieceSize]--
            // Esconde a peça quando não houver mais peças daquele tamanho
            if (player2Pieces[pieceSize] == 0) {
                hidePiece(piece, binding.jog2PiecesLayout)
            }
        }

        // Verifica se o jogador venceu
        if (checkWinCondition()) {
            Toast.makeText(this, "Jogador $currentPlayer venceu!", Toast.LENGTH_LONG).show()
            resetGame()
            return
        }

        // Alterna o turno
        switchTurn()
    }

    private fun hidePiece(piece: ImageView, playerPiecesLayout: View) {
        piece.visibility = View.INVISIBLE

        // Verificar e ocultar as peças do jogador dentro do layout
        if (playerPiecesLayout is ViewGroup) {
            for (i in 0 until playerPiecesLayout.childCount) {
                val item = playerPiecesLayout.getChildAt(i)
                if (item == piece) {
                    item.visibility = View.INVISIBLE
                }
            }
        }
    }


    private fun hasAvailablePieces(player: Int, piece: View): Boolean {
        val pieceSize = when (piece.contentDescription.toString()) {
            "Peça pequena jogador 1", "Peça pequena jogador 2" -> 0
            "Peça média jogador 1", "Peça média jogador 2" -> 1
            "Peça grande jogador 1", "Peça grande jogador 2" -> 2
            else -> -1
        }

        return if (player == 1) {
            player1Pieces[pieceSize] > 0
        } else {
            player2Pieces[pieceSize] > 0
        }
    }

    private fun checkWinCondition(): Boolean {
        // Verificar linhas
        for (i in 0..2) {
            if (board[i].all { it.isNotEmpty() && it.last().second == currentPlayer }) return true
        }
        // Verificar colunas
        for (i in 0..2) {
            if ((0..2).all { board[it][i].isNotEmpty() && board[it][i].last().second == currentPlayer }) return true
        }
        // Verificar diagonal principal
        if ((0..2).all { board[it][it].isNotEmpty() && board[it][it].last().second == currentPlayer }) return true
        // Verificar diagonal secundária
        if ((0..2).all { board[it][2 - it].isNotEmpty() && board[it][2 - it].last().second == currentPlayer }) return true

        return false
    }

    private fun switchTurn() {
        currentPlayer = if (currentPlayer == 1) 2 else 1
        updateTurnIndicator()
        selectedPiece = null
    }

    private fun updateTurnIndicator() {
        binding.turnIndicator.text = "Jogador $currentPlayer: Sua vez!"
    }

    private fun resetGame() {
        // Limpar o tabuleiro
        board.forEach { row -> row.forEach { it.clear() } }
        // Resetar as peças dos jogadores
        player1Pieces.fill(3)
        player2Pieces.fill(3)
        // Atualizar UI
        currentPlayer = 1
        updateTurnIndicator()
        // Tornar as peças visíveis novamente
        binding.jog1PiecesLayout.forEach { it.visibility = View.VISIBLE }
        binding.jog2PiecesLayout.forEach { it.visibility = View.VISIBLE }
    }
}
