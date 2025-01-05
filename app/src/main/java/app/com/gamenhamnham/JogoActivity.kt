package app.com.gamenhamnham

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import app.com.gamenhamnham.databinding.ActivityJogoBinding

class JogoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJogoBinding
    private var currentPlayer = 1 // 1 para jogador 1, 2 para jogador 2
    private val board = Array(3) { Array(3) { mutableListOf<Pair<Int, Int>>() } } // Cada célula contém uma lista de pares (tamanho da peça, jogador)
    private var selectedPiece: ImageView? = null  // Variável para armazenar a peça selecionada
    private val player1Pieces = intArrayOf(3, 3, 3) // [pequena, média, grande]
    private val player2Pieces = intArrayOf(3, 3, 3) // [pequena, média, grande]
    private var gameFinished = false

    private var player1Points = 0
    private var player2Points = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJogoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()  // Configura os listeners de clique
        updateTurnIndicator()  // Atualiza o indicador de turno

        binding.backButton.setOnClickListener {
            finish()
        }

        // Listener para o botão de reinício
        binding.buttonReset.setOnClickListener {
            resetGame()  // Chama a função para reiniciar o jogo
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
        // Remove o destaque da peça previamente selecionada
        selectedPiece?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))

        // Atualiza a peça selecionada
        selectedPiece = piece
        piece.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_200)) // Destaque da peça atual
    }

    private fun handlePieceDrop(cell: ImageView, piece: ImageView, row: Int, col: Int) {
        if (gameFinished) {
            Toast.makeText(this, "O jogo já terminou! Reinicie para jogar novamente.", Toast.LENGTH_SHORT).show()
            return
        }

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

        // Verificar se há uma peça existente na célula
        if (board[row][col].isNotEmpty()) {
            val existingPiece = board[row][col].last()
            // Se a peça existente for menor, ela é "comida"
            if (existingPiece.first < pieceSize && existingPiece.second != currentPlayer) {
                // "Comer" a peça - devolver ao jogador
                val removedPieceSize = existingPiece.first
                if (existingPiece.second == 1) {
                    player1Pieces[removedPieceSize]++
                    updatePieceCount(player1Pieces, 1)
                } else {
                    player2Pieces[removedPieceSize]++
                    updatePieceCount(player2Pieces, 2)
                }

                // Reexibir a peça que foi comida para o jogador original
                if (existingPiece.second == 1) {
                    binding.jog1PiecesLayout[removedPieceSize].visibility = View.VISIBLE
                } else {
                    binding.jog2PiecesLayout[removedPieceSize].visibility = View.VISIBLE
                }

                // Exibir mensagem de que a peça foi comida
                Toast.makeText(this, "A peça foi comida e voltou para a mão do jogador ${existingPiece.second}!", Toast.LENGTH_SHORT).show()
            } else if (existingPiece.first >= pieceSize) {
                // Se a peça não for comida, é uma jogada inválida
                Toast.makeText(this, "Você não pode colocar uma peça menor aqui!", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Adiciona a peça à célula
        board[row][col].add(Pair(pieceSize, currentPlayer)) // Adiciona a peça ao tabuleiro com seu jogador associado
        cell.setImageDrawable(piece.drawable) // Coloca a peça na célula do tabuleiro

        // Atualiza a quantidade de peças restantes
        if (currentPlayer == 1) {
            player1Pieces[pieceSize]--
            updatePieceCount(player1Pieces, 1) // Atualiza os contadores de peças do jogador 1
        } else {
            player2Pieces[pieceSize]--
            updatePieceCount(player2Pieces, 2) // Atualiza os contadores de peças do jogador 2
        }

        // Verifica se o jogador venceu
        if (checkWinCondition()) {
            handleWin() // Atualiza o placar quando um jogador vence
            Toast.makeText(this, "Jogador $currentPlayer venceu!", Toast.LENGTH_LONG).show()
            gameFinished = true // Marca o jogo como finalizado
            endGame() // Chama a função para desabilitar as interações e exibir a mensagem
            piece.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
            return
        }

        piece.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))

        // Alterna o turno
        switchTurn()
    }

    private fun updatePieceCount(playerPieces: IntArray, player: Int) {
        val smallCount = playerPieces[0]
        val mediumCount = playerPieces[1]
        val largeCount = playerPieces[2]

        if (player == 1) {
            binding.jog1SmallCount.text = smallCount.toString()
            binding.jog1MediumCount.text = mediumCount.toString()
            binding.jog1LargeCount.text = largeCount.toString()
        } else {
            binding.jog2SmallCount.text = smallCount.toString()
            binding.jog2MediumCount.text = mediumCount.toString()
            binding.jog2LargeCount.text = largeCount.toString()
        }
    }

    private fun handleWin() {
        if (currentPlayer == 1) {
            player1Points++
            binding.pointsPlayer1.text = player1Points.toString()  // Atualiza o placar do jogador 1
        } else {
            player2Points++
            binding.pointsPlayer2.text = player2Points.toString()  // Atualiza o placar do jogador 2
        }
    }

    private fun resetGame() {
        // Limpar o tabuleiro
        board.forEach { row -> row.forEach { it.clear() } }

        // Resetar as peças dos jogadores
        player1Pieces.fill(3)
        player2Pieces.fill(3)

        // Atualizar os contadores de peças visíveis
        updatePieceCount(player1Pieces, 1)
        updatePieceCount(player2Pieces, 2)

        // Atualizar o turno para o Jogador 1
        currentPlayer = 1
        updateTurnIndicator() // Atualiza o indicador de turno

        // Tornar as peças visíveis novamente para os dois jogadores
        binding.jog1PiecesLayout.forEach { it.visibility = View.VISIBLE }
        binding.jog2PiecesLayout.forEach { it.visibility = View.VISIBLE }

        // Tornar as células do tabuleiro clicáveis e limpas
        for (row in 0..2) {
            for (col in 0..2) {
                val cellId = "cell_${row}${col}"
                val cell = binding.root.findViewById<ImageView>(resources.getIdentifier(cellId, "id", packageName))
                cell.isClickable = true // Habilita novamente as células
                cell.setImageDrawable(null)  // Limpa as imagens das células
            }
        }

        // Resetar o estado do jogo
        gameFinished = false

        // Remover a seleção da peça anteriormente selecionada
        selectedPiece?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        selectedPiece = null

        // Reiniciar as peças dos jogadores: Habilitar as peças para seleção novamente
        binding.jog1PiecesLayout.forEach { piece ->
            piece.isClickable = true // Tornar as peças do jogador 1 clicáveis
            piece.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent)) // Remover qualquer destaque de seleção anterior
        }

        binding.jog2PiecesLayout.forEach { piece ->
            piece.isClickable = true // Tornar as peças do jogador 2 clicáveis
            piece.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent)) // Remover qualquer destaque de seleção anterior
        }
    }



    private fun endGame() {
        // Desabilitar todos os listeners de clique
        binding.jog1PiecesLayout.forEach { it.isClickable = false }
        binding.jog2PiecesLayout.forEach { it.isClickable = false }

        // Desabilitar as células do tabuleiro
        for (row in 0..2) {
            for (col in 0..2) {
                val cellId = "cell_${row}${col}"
                val cell = binding.root.findViewById<ImageView>(resources.getIdentifier(cellId, "id", packageName))
                cell.isClickable = false
            }
        }

    }

    private fun switchTurn() {
        currentPlayer = if (currentPlayer == 1) 2 else 1
        updateTurnIndicator()
    }

    private fun updateTurnIndicator() {
        if (currentPlayer == 1) {
            binding.turnIndicator.text = "Jogador 1 é a vez"
        } else {
            binding.turnIndicator.text = "Jogador 2 é a vez"
        }
    }

    private fun checkWinCondition(): Boolean {
        // Verificar linhas
        for (row in 0..2) {
            if (board[row][0].isNotEmpty() && board[row][0].last().second == currentPlayer &&
                board[row][1].isNotEmpty() && board[row][1].last().second == currentPlayer &&
                board[row][2].isNotEmpty() && board[row][2].last().second == currentPlayer) {
                return true // Vitória na linha
            }
        }

        // Verificar colunas
        for (col in 0..2) {
            if (board[0][col].isNotEmpty() && board[0][col].last().second == currentPlayer &&
                board[1][col].isNotEmpty() && board[1][col].last().second == currentPlayer &&
                board[2][col].isNotEmpty() && board[2][col].last().second == currentPlayer) {
                return true // Vitória na coluna
            }
        }

        // Verificar diagonal principal
        if (board[0][0].isNotEmpty() && board[0][0].last().second == currentPlayer &&
            board[1][1].isNotEmpty() && board[1][1].last().second == currentPlayer &&
            board[2][2].isNotEmpty() && board[2][2].last().second == currentPlayer) {
            return true // Vitória na diagonal principal
        }

        // Verificar diagonal secundária
        if (board[0][2].isNotEmpty() && board[0][2].last().second == currentPlayer &&
            board[1][1].isNotEmpty() && board[1][1].last().second == currentPlayer &&
            board[2][0].isNotEmpty() && board[2][0].last().second == currentPlayer) {
            return true // Vitória na diagonal secundária
        }

        return false // Nenhuma condição de vitória encontrada
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
}
