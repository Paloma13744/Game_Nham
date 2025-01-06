# Nham Nham

**Nham Nham** é um jogo de tabuleiro inspirado no clássico **Nhac Nhac**. Com uma jogabilidade estratégica e inovadora, o objetivo é alinhar três peças do mesmo jogador em sequência, seja na horizontal, vertical 
ou diagonal. O jogo oferece uma experiência única ao permitir que peças maiores se sobreponham a peças menores no tabuleiro.

![image](https://github.com/user-attachments/assets/3420658c-3765-4ab6-b25f-1a17c9f81645)

## 📋 Regras do Jogo

- O jogo é jogado em um tabuleiro 3x3 com 18 peças (9 para cada jogador).
- Cada jogador possui três tamanhos de peças: pequeno, médio e grande.
- Jogadores alternam turnos para posicionar peças no tabuleiro.
- Peças maiores podem sobrepor peças menores.
- Ganha quem alinhar três peças consecutivas (mesmo tamanho e jogador) ou, caso o tabuleiro fique cheio, o jogo termina empatado.

## 🛠️ Funcionalidades

### **MainActivity**
- Configuração inicial e controle da interface principal.
- Botões para iniciar o jogo, visualizar regras e controlar a música de fundo.
- Gerenciamento de áudio: play, pause, próximo, silenciar e troca de faixas musicais.

### **RegrasActivity**
- Tela dedicada para exibição das regras do jogo.
- Botão para retornar à tela inicial.

### **tentarSorteActivity**
- Implementa um sorteio de "Cara ou Coroa" antes de iniciar o jogo.
- Exibe vídeo temático e resultado do sorteio.

### **JogoActivity**
- Lógica central do jogo de tabuleiro:
  - Alternância de turnos.
  - Posicionamento e gerenciamento de peças.
  - Verificação de vitória ou empate.
  - Reinício do jogo e gerenciamento do estado do tabuleiro.

### **Layouts**
- **Tela principal:** Acesso às principais funções do jogo (iniciar, regras, controle de som).
- **Tela de regras:** Exibe instruções detalhadas para os jogadores.
- **Tela de sorteio:** Simula o sorteio de "Cara ou Coroa".
- **Tela do jogo:** Interface completa com tabuleiro, peças e contadores.

## 🎮 Jogabilidade

1. **Sorteio Inicial**:
   - Antes de começar, os jogadores realizam um sorteio de "Cara ou Coroa" para decidir quem inicia.
2. **Turnos Alternados**:
   - Jogadores escolhem e posicionam peças no tabuleiro.
   - Estratégias envolvem sobrepor peças menores com maiores.
3. **Condição de Vitória**:
   - Vitória ocorre ao alinhar três peças consecutivas ou ao preencher o tabuleiro sem vencedores (empate).

## 🚀 Tecnologias Utilizadas

- 📝 **Linguagem:** [Kotlin](https://kotlinlang.org/)  
- 📱 **Plataforma:** [Android Studio](https://developer.android.com/studio)  
- 🎵 **Recursos:**
  - 🎧 [MediaPlayer](https://developer.android.com/reference/android/media/MediaPlayer) para controle de áudio  
  - 🎮 [GridLayout](https://developer.android.com/reference/android/widget/GridLayout) para a interface do tabuleiro  
  - 📦 [ActivityMainBinding](https://developer.android.com/topic/libraries/view-binding) para gerenciamento de interfaces 

## 👥 Autores

- [**Paloma13744**](https://github.com/Paloma13744)  
- [**ThSFernandes**](https://github.com/ThSFernandes)  

