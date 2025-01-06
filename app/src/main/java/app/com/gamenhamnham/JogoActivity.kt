package app.com.gamenhamnham

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
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
    private var jogadorAtual = 1
    private val tabuleiro =
        Array(3) { Array(3) { mutableListOf<Pair<Int, Int>>() } }
    private var selecioanarPeca: ImageView? = null
    private val pecasJogador1 = intArrayOf(3, 3, 3)
    private val pecasJogador2 = intArrayOf(3, 3, 3)
    private var finalizarRodada = false

    private var pontosJogador1 = 0
    private var pontosJogador2 = 0
    private var pontosEmpate = 0

    private var vencedorAnterior = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJogoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val escolhaJogador1 = intent.getStringExtra("escolhaJogador1")
        val resultadoSorteio = intent.getStringExtra("resultadoSorteio")

        Log.d("DEBUG", "escolhaJogador1: $escolhaJogador1, resultadoSorteio: $resultadoSorteio")

        if (escolhaJogador1 == null || resultadoSorteio == null) {
            Toast.makeText(this, "Erro ao carregar informações do sorteio!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        jogadorAtual = if (escolhaJogador1 == resultadoSorteio) 1 else 2

        atualizarTextoDeTurno()

        gerenciarToquesPecas()
        atualizarTextoDeTurno()

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.buttonReset.setOnClickListener {
            reiniciarJogo()
        }

    }

    private fun gerenciarToquesPecas() {
        // Configurar os cliques nas peças do jogador 1
        binding.jog1PiecesLayout.forEach { piece ->
            piece.setOnClickListener {
                if (jogadorAtual != 1) {
                    Toast.makeText(this, "Não é sua vez!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (!verificarPecasDisponiveis(1, piece)) {
                    Toast.makeText(
                        this,
                        "Você não tem mais peças deste tamanho!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                selecionarPeca(it as ImageView)
            }
        }

        binding.jog2PiecesLayout.forEach { piece ->
            piece.setOnClickListener {
                if (jogadorAtual != 2) {
                    Toast.makeText(this, "Não é sua vez!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (!verificarPecasDisponiveis(2, piece)) {
                    Toast.makeText(
                        this,
                        "Você não tem mais peças deste tamanho!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                selecionarPeca(it as ImageView)
            }
        }

        for (row in 0..2) {
            for (col in 0..2) {
                val cellId = "cell_${row}${col}"
                val cell = binding.root.findViewById<ImageView>(
                    resources.getIdentifier(
                        cellId,
                        "id",
                        packageName
                    )
                )
                cell.setOnClickListener {
                    if (selecioanarPeca != null) {
                        gerenciarPecas(cell, selecioanarPeca!!, row, col)
                    } else {
                        Toast.makeText(this, "Selecione uma peça primeiro!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }


    private fun selecionarPeca(piece: ImageView) {
        selecioanarPeca?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))

        selecioanarPeca = piece
        piece.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.teal_200
            )
        )
    }

    private fun gerenciarPecas(cell: ImageView, piece: ImageView, row: Int, col: Int) {
        if (finalizarRodada) {
            Toast.makeText(
                this,
                "O jogo já terminou! Para jogar novamente aperte o botão acima.",
                Toast.LENGTH_SHORT
            ).show()
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

        if (tabuleiro[row][col].isNotEmpty()) {
            val existingPiece = tabuleiro[row][col].last()
            if (existingPiece.first < pieceSize && existingPiece.second != jogadorAtual) {

                tocarSomSobreposicao()
                val removedPieceSize = existingPiece.first
                if (existingPiece.second == 1) {
                    pecasJogador1[removedPieceSize]++
                    atualizarContadorPecas(pecasJogador1, 1)
                } else {
                    pecasJogador2[removedPieceSize]++
                    atualizarContadorPecas(pecasJogador2, 2)
                }

                if (existingPiece.second == 1) {
                    binding.jog1PiecesLayout[removedPieceSize].visibility = View.VISIBLE
                } else {
                    binding.jog2PiecesLayout[removedPieceSize].visibility = View.VISIBLE
                }

                Toast.makeText(
                    this,
                    "A peça foi comida e voltou para a mão do jogador ${existingPiece.second}!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (existingPiece.first >= pieceSize) {
                Toast.makeText(
                    this,
                    "Você precisa escolher uma peça maior para posicionar!",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        tabuleiro[row][col].add(Pair(pieceSize, jogadorAtual))
        cell.setImageDrawable(piece.drawable)

        if (jogadorAtual == 1) {
            pecasJogador1[pieceSize]--
            atualizarContadorPecas(pecasJogador1, 1)
        } else {
            pecasJogador2[pieceSize]--
            atualizarContadorPecas(pecasJogador2, 2)
        }

        // Se a peça foi colocada, desmarque a seleção
        selecioanarPeca?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        selecioanarPeca = null

        if (verificarVitoria()) {
            verificaVencedor()
            Toast.makeText(this, "Jogador $jogadorAtual venceu!", Toast.LENGTH_LONG).show()
            finalizarRodada = true
            finalizarJogo()
            return
        }

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
                tabuleiro[row][2].isNotEmpty() && tabuleiro[row][2].last().second == jogadorAtual
            ) {
                return true
            }
        }

        // Verificar colunas
        for (col in 0..2) {
            if (tabuleiro[0][col].isNotEmpty() && tabuleiro[0][col].last().second == jogadorAtual &&
                tabuleiro[1][col].isNotEmpty() && tabuleiro[1][col].last().second == jogadorAtual &&
                tabuleiro[2][col].isNotEmpty() && tabuleiro[2][col].last().second == jogadorAtual
            ) {
                return true
            }
        }

        if (tabuleiro[0][0].isNotEmpty() && tabuleiro[0][0].last().second == jogadorAtual &&
            tabuleiro[1][1].isNotEmpty() && tabuleiro[1][1].last().second == jogadorAtual &&
            tabuleiro[2][2].isNotEmpty() && tabuleiro[2][2].last().second == jogadorAtual
        ) {
            return true
        }

        if (tabuleiro[0][2].isNotEmpty() && tabuleiro[0][2].last().second == jogadorAtual &&
            tabuleiro[1][1].isNotEmpty() && tabuleiro[1][1].last().second == jogadorAtual &&
            tabuleiro[2][0].isNotEmpty() && tabuleiro[2][0].last().second == jogadorAtual
        ) {
            return true
        }

        if (tabuleiroCompleto() && !existeMovimentoValido()) {
            Toast.makeText(this, "Empate!", Toast.LENGTH_LONG).show()
            pontosEmpate++
            binding.pointsEmpates.text = pontosEmpate.toString()
            finalizarRodada = true
            return false
        }

        return false
    }


    private fun tabuleiroCompleto(): Boolean {
        for (row in 0..2) {
            for (col in 0..2) {
                if (tabuleiro[row][col].isEmpty()) {
                    return false
                }
            }
        }
        return true
    }

    private fun existeMovimentoValido(): Boolean {
        for (row in 0..2) {
            for (col in 0..2) {
                val cell = tabuleiro[row][col]
                if (cell.isNotEmpty()) {
                    val pieceSize = cell.last().first
                    val pieceOwner = cell.last().second


                    if (pieceOwner != jogadorAtual) {

                        val pecasMaiores = if (jogadorAtual == 1) {
                            pecasJogador1
                        } else {
                            pecasJogador2
                        }


                        for (tamanho in 2 downTo 1) {
                            if (pecasMaiores[tamanho] > 0 && pieceSize < tamanho) {
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }


    private fun verificaVencedor() {

        if (verificarVitoria()) {
            if (jogadorAtual == 1) {
                pontosJogador1++
                binding.pointsPlayer1.text = pontosJogador1.toString()
                vencedorAnterior = 1
            } else {
                pontosJogador2++
                binding.pointsPlayer2.text = pontosJogador2.toString()
                vencedorAnterior = 2
            }
        } else {

            vencedorAnterior = 0
        }
    }


    private fun reiniciarJogo() {

        tabuleiro.forEach { row -> row.forEach { it.clear() } }


        pecasJogador1.fill(3)
        pecasJogador2.fill(3)

        atualizarContadorPecas(pecasJogador1, 1)
        atualizarContadorPecas(pecasJogador2, 2)

        jogadorAtual = if (vencedorAnterior == 0) {

            jogadorAtual
        } else {
            if (vencedorAnterior == 1) 2 else 1
        }

        atualizarTextoDeTurno()

        binding.jog1PiecesLayout.forEach { it.visibility = View.VISIBLE }
        binding.jog2PiecesLayout.forEach { it.visibility = View.VISIBLE }

        for (row in 0..2) {
            for (col in 0..2) {
                val cellId = "cell_${row}${col}"
                val cell = binding.root.findViewById<ImageView>(
                    resources.getIdentifier(cellId, "id", packageName)
                )
                cell.isClickable = true
                cell.setImageDrawable(null)
            }
        }

        finalizarRodada = false
        selecioanarPeca?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        selecioanarPeca = null

        binding.jog1PiecesLayout.forEach { piece ->
            piece.isClickable = true
            piece.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        }

        binding.jog2PiecesLayout.forEach { piece ->
            piece.isClickable = true
            piece.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        }
    }



    private fun finalizarJogo() {
        binding.jog1PiecesLayout.forEach { it.isClickable = false }
        binding.jog2PiecesLayout.forEach { it.isClickable = false }

        for (row in 0..2) {
            for (col in 0..2) {
                val cellId = "cell_${row}${col}"
                val cell = binding.root.findViewById<ImageView>(
                    resources.getIdentifier(
                        cellId,
                        "id",
                        packageName
                    )
                )
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

    private fun tocarSomSobreposicao() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.comendo)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release() // Libera os recursos após tocar
        }
    }
}