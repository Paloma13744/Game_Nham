package app.com.gamenhamnham

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.view.get
import app.com.gamenhamnham.databinding.ActivityJogoBinding

class JogoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJogoBinding
    private var jogadorAtual = 1 // 1 para jogador 1, 2 para jogador 2
    private val tabuleiro = Array(3) { Array(3) { mutableListOf<Pair<Int, Int>>() } } // Cada célula contém uma lista de pares (tamanho da peça, jogador)
    private var selecioanarPeca: ImageView? = null  // Variável para armazenar a peça selecionada
    private val pecasJogador1 = intArrayOf(3, 3, 3) // [pequena, média, grande]
    private val pecasJogador2 = intArrayOf(3, 3, 3) // [pequena, média, grande]
    private var finalizarRodada = false

    private var pontosJogador1 = 0
    private var pontosJogador2 = 0
    private var pontosEmpate = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJogoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()  // Configura os listeners de clique
        atualizarTextoDeTurno()  // Atualiza o indicador de turno

        binding.backButton.setOnClickListener {
            finish()
        }

        // Listener para o botão de reinício
        binding.buttonReset.setOnClickListener {
            reiniciarJogo()  // Chama a função para reiniciar o jogo
        }
    }

    private fun setupClickListeners() {
        // Configurar os cliques nas peças do jogador 1
        binding.jog1PiecesLayout.forEach { piece ->
            piece.setOnClickListener {
                if (jogadorAtual == 1 && verificarPecasDisponiveis(1, piece)) {
                    selecionarPeca(it as ImageView)
                } else {
                    Toast.makeText(this, "Não é sua vez ou você não tem mais peças deste tamanho!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configurar os cliques nas peças do jogador 2
        binding.jog2PiecesLayout.forEach { piece ->
            piece.setOnClickListener {
                if (jogadorAtual == 2 && verificarPecasDisponiveis(2, piece)) {
                    selecionarPeca(it as ImageView)
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
                    if (selecioanarPeca != null) {
                        gerenciarPecas(cell, selecioanarPeca!!, row, col)
                    } else {
                        Toast.makeText(this, "Selecione uma peça primeiro!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun selecionarPeca(piece: ImageView) {
        // Remove o destaque da peça previamente selecionada
        selecioanarPeca?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))

        // Atualiza a peça selecionada
        selecioanarPeca = piece
        piece.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_200)) // Destaque da peça atual
    }

    private fun gerenciarPecas(cell: ImageView, piece: ImageView, row: Int, col: Int) {
        if (finalizarRodada) {
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
        if (tabuleiro[row][col].isNotEmpty()) {
            val existingPiece = tabuleiro[row][col].last()
            // Se a peça existente for menor, ela é "comida"
            if (existingPiece.first < pieceSize && existingPiece.second != jogadorAtual) {
                // "Comer" a peça - devolver ao jogador
                val removedPieceSize = existingPiece.first
                if (existingPiece.second == 1) {
                    pecasJogador1[removedPieceSize]++
                    atualizarContadorPecas(pecasJogador1, 1)
                } else {
                    pecasJogador2[removedPieceSize]++
                    atualizarContadorPecas(pecasJogador2, 2)
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
        tabuleiro[row][col].add(Pair(pieceSize, jogadorAtual)) // Adiciona a peça ao tabuleiro com seu jogador associado
        cell.setImageDrawable(piece.drawable) // Coloca a peça na célula do tabuleiro

        // Atualiza a quantidade de peças restantes
        if (jogadorAtual == 1) {
            pecasJogador1[pieceSize]--
            atualizarContadorPecas(pecasJogador1, 1) // Atualiza os contadores de peças do jogador 1
        } else {
            pecasJogador2[pieceSize]--
            atualizarContadorPecas(pecasJogador2, 2) // Atualiza os contadores de peças do jogador 2
        }

        // Verifica se o jogo terminou por vitória ou empate
        if (verificarVitoria()) {
            verificaVencedor() // Atualiza o placar quando um jogador vence
            Toast.makeText(this, "Jogador $jogadorAtual venceu!", Toast.LENGTH_LONG).show()
            finalizarRodada = true // Marca o jogo como finalizado
            finalizarJogo() // Chama a função para desabilitar as interações e exibir a mensagem
            piece.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
            return
        }

        piece.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))

        // Alterna o turno
        mudarTurno()
    }
    private fun atualizarContadorPecas(playerPieces: IntArray, player: Int) {
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

    private fun verificarVitoria(): Boolean {
        // Verificar linhas
        for (row in 0..2) {
            if (tabuleiro[row][0].isNotEmpty() && tabuleiro[row][0].last().second == jogadorAtual &&
                tabuleiro[row][1].isNotEmpty() && tabuleiro[row][1].last().second == jogadorAtual &&
                tabuleiro[row][2].isNotEmpty() && tabuleiro[row][2].last().second == jogadorAtual) {
                return true // Vitória na linha
            }
        }

        // Verificar colunas
        for (col in 0..2) {
            if (tabuleiro[0][col].isNotEmpty() && tabuleiro[0][col].last().second == jogadorAtual &&
                tabuleiro[1][col].isNotEmpty() && tabuleiro[1][col].last().second == jogadorAtual &&
                tabuleiro[2][col].isNotEmpty() && tabuleiro[2][col].last().second == jogadorAtual) {
                return true // Vitória na coluna
            }
        }

        // Verificar diagonal principal
        if (tabuleiro[0][0].isNotEmpty() && tabuleiro[0][0].last().second == jogadorAtual &&
            tabuleiro[1][1].isNotEmpty() && tabuleiro[1][1].last().second == jogadorAtual &&
            tabuleiro[2][2].isNotEmpty() && tabuleiro[2][2].last().second == jogadorAtual) {
            return true // Vitória na diagonal principal
        }

        // Verificar diagonal secundária
        if (tabuleiro[0][2].isNotEmpty() && tabuleiro[0][2].last().second == jogadorAtual &&
            tabuleiro[1][1].isNotEmpty() && tabuleiro[1][1].last().second == jogadorAtual &&
            tabuleiro[2][0].isNotEmpty() && tabuleiro[2][0].last().second == jogadorAtual) {
            return true // Vitória na diagonal secundária
        }

        // Verificar empate
        var empate = true
        for (row in 0..2) {
            for (col in 0..2) {
                if (tabuleiro[row][col].isEmpty()) {
                    empate = false // Se houver alguma célula vazia, não é empate
                    break
                }
            }
        }

        if (empate) {
            Toast.makeText(this, "Empate!", Toast.LENGTH_LONG).show()
            pontosEmpate++  // Incrementa os pontos de empate
            binding.pointsEmpates.text = pontosEmpate.toString()
            finalizarRodada = true  // Finaliza a rodada
        }

        return false // Nenhuma condição de vitória encontrada
    }



    private fun verificaVencedor() {
        // Verifica se algum jogador venceu
        if (verificarVitoria()) {
            if (jogadorAtual == 1) {
                pontosJogador1++
                binding.pointsPlayer1.text = pontosJogador1.toString()
            } else {
                pontosJogador2++
                binding.pointsPlayer2.text = pontosJogador2.toString()
            }
        }
        // A verificação de empate já ocorre na função verificarVitoria(), então não é necessário aqui
    }


    private fun reiniciarJogo() {
        // Limpar o tabuleiro
        tabuleiro.forEach { row -> row.forEach { it.clear() } }

        // Resetar as peças dos jogadores
        pecasJogador1.fill(3)
        pecasJogador2.fill(3)

        // Atualizar os contadores de peças visíveis
        atualizarContadorPecas(pecasJogador1, 1)
        atualizarContadorPecas(pecasJogador2, 2)

        // Atualizar o turno para o Jogador 1
        jogadorAtual = 1
        atualizarTextoDeTurno() // Atualiza o indicador de turno

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
        finalizarRodada = false

        // Remover a seleção da peça anteriormente selecionada
        selecioanarPeca?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        selecioanarPeca = null

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



    private fun finalizarJogo() {
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

    private fun mudarTurno() {
        jogadorAtual = if (jogadorAtual == 1) 2 else 1
        atualizarTextoDeTurno()
    }

    private fun atualizarTextoDeTurno() {
        if (jogadorAtual == 1) {
            binding.turnIndicator.text = "Jogador 1 é a vez"
        } else {
            binding.turnIndicator.text = "Jogador 2 é a vez"
        }
    }


    private fun verificarPecasDisponiveis(player: Int, piece: View): Boolean {
        val pieceSize = when (piece.contentDescription.toString()) {
            "Peça pequena jogador 1", "Peça pequena jogador 2" -> 0
            "Peça média jogador 1", "Peça média jogador 2" -> 1
            "Peça grande jogador 1", "Peça grande jogador 2" -> 2
            else -> -1
        }
        return if (player == 1) {
            pecasJogador1[pieceSize] > 0
        } else {
            pecasJogador2[pieceSize] > 0
        }
    }
}
