package app.com.gamenhamnham

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import app.com.gamenhamnham.databinding.ActivityJogoBinding

class JogoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJogoBinding
    private var currentPlayer = 1 // 1 para jogador 1, 2 para jogador 2
    private val board = Array(3) { Array(3) { mutableListOf<Int>() } } // Representa as pilhas em cada célula do tabuleiro
    private var selectedPiece: ImageView? = null  // Variável para armazenar a peça selecionada
    private val player1Pieces = intArrayOf(3, 3, 3) // [pequena, média, grande]
    private val player2Pieces = intArrayOf(3, 3, 3) // [pequena, média, grande]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJogoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()  // Configura os listeners de clique
        updateTurnIndicator()  // Atualiza o indicador de turno
    }

    private fun setupClickListeners() {
        // Configurar os cliques nas peças do jogador 1
        binding.jog1PiecesLayout.forEach { piece ->
            piece.setOnClickListener {
                if (currentPlayer == 1 && player1Pieces.any { it > 0 }) {
                    selectPiece(it as ImageView)
                } else {
                    Toast.makeText(this, "Não é sua vez ou você não tem mais peças!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configurar os cliques nas peças do jogador 2
        binding.jog2PiecesLayout.forEach { piece ->
            piece.setOnClickListener {
                if (currentPlayer == 2 && player2Pieces.any { it > 0 }) {
                    selectPiece(it as ImageView)
                } else {
                    Toast.makeText(this, "Não é sua vez ou você não tem mais peças!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configurar os cliques nas células do tabuleiro
        for (row in 0..2) {
            for (col in 0..2) {
                val cellId = "cell_${row}${col}"
                val cell = binding.root.findViewById<ImageView>(
                    resources.getIdentifier(cellId, "id", packageName)
                )
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
            "Peça pequena jogador 1", "Peça pequena jogador 2" -> 1
            "Peça média jogador 1", "Peça média jogador 2" -> 2
            "Peça grande jogador 1", "Peça grande jogador 2" -> 3
            else -> 0
        }

        if (board[row][col].isNotEmpty() && board[row][col].last() >= pieceSize) {
            Toast.makeText(this, "Você não pode colocar uma peça menor aqui!", Toast.LENGTH_SHORT).show()
            return
        }

        board[row][col].add(pieceSize)
        cell.setImageDrawable(piece.drawable)  // Coloca a peça na célula do tabuleiro
        piece.visibility = View.INVISIBLE // Remove a peça da posição do jogador

        if (currentPlayer == 1) {
            player1Pieces[pieceSize - 1]--
        } else {
            player2Pieces[pieceSize - 1]--
        }

        if (checkWinCondition()) {
            Toast.makeText(this, "Jogador $currentPlayer venceu!", Toast.LENGTH_LONG).show()
            return
        }

        switchTurn()
    }

    private fun checkWinCondition(): Boolean {
        for (i in 0..2) {
            if (board[i].all { it.isNotEmpty() && it.last() == currentPlayer }) return true
            if ((0..2).all { board[it][i].isNotEmpty() && board[it][i].last() == currentPlayer }) return true
        }
        if ((0..2).all { board[it][it].isNotEmpty() && board[it][it].last() == currentPlayer }) return true
        if ((0..2).all { board[it][2 - it].isNotEmpty() && board[it][2 - it].last() == currentPlayer }) return true

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
}