package agente;

import algoritmo.ProgramaPoupador;

public class Poupador extends ProgramaPoupador {

    private static final int LIMITE_MOEDAS_PARA_IR_AO_BANCO = 10; // Novo limite de moedas para guardar no banco
    private static final int PASSOS_DE_FUGA = 7; // Número de passos que o poupador dá ao fugir de um ladrão
    private boolean parado = false; // Variável para controlar se o poupador deve ficar parado

    public int acao() {
        int[] visao = sensor.getVisaoIdentificacao(); // A visão deve ser de 7x7
        int[] olfatoLadrao = sensor.getAmbienteOlfatoLadrao();
        int moedas = sensor.getNumeroDeMoedas();

        // Se o poupador já está em modo "parado", ele não se move
        if (parado) {
            return 0; // Ficar parado
        }

        // Se há ladrão próximo, tentar pegar a pastilha do poder ou desviar
        if (deveDesviarDeLadrao(olfatoLadrao, visao)) {
            if (contains(visao, 5)) { // Se houver uma pastilha do poder visível
                return moverParaPastilha(visao);
            }
            return fugirDeLadrao(olfatoLadrao);
        }

        // Se o poupador tem muitas moedas, ir ao banco
        if (moedas >= LIMITE_MOEDAS_PARA_IR_AO_BANCO) {
            // Tenta se mover para o banco
            int movimentoBanco = moverParaBanco(visao);

            // Se o banco estiver próximo e ele conseguir guardar as moedas, então fica parado
            if (movimentoBanco == 0) { // Supondo que 0 indica que ele chegou ao banco
                parado = true; // O poupador agora ficará parado
                return 0; // Ficar parado
            }
            return movimentoBanco; // Continua indo para o banco
        }

        // Priorizar coleta de moedas
        if (contains(visao, 4)) {
            return moverParaMoeda(visao);
        }

        // Movimentação aleatória, mas sem ficar parado
        return movimentoAleatorioSemParar();
    }

    // Função para mover em direção ao banco
    private int moverParaBanco(int[] visao) {
        int posicaoBanco = -1;

        // Procurar pelo banco (valor 3 na visão)
        for (int i = 0; i < visao.length; i++) {
            if (visao[i] == 3) { // Se encontrar o banco
                posicaoBanco = i;
                break;
            }
        }

        // Se o banco não for visível, continuar se movendo aleatoriamente
        if (posicaoBanco == -1) {
            return movimentoAleatorioSemParar();
        }

        // Se o poupador está no banco (posição 12 na visão é o poupador)
        if (posicaoBanco == 12) {
            return 0; // Ficar parado no banco
        }

        // Determinar o movimento com base na posição do banco
        return determinarMovimento(posicaoBanco);
    }

    // Função para mover em direção a uma pastilha do poder (valor 5)
    private int moverParaPastilha(int[] visao) {
        int posicaoPastilha = -1;

        // Procurar pela pastilha do poder (valor 5 na visão)
        for (int i = 0; i < visao.length; i++) {
            if (visao[i] == 5) { // Se encontrar a pastilha
                posicaoPastilha = i;
                break;
            }
        }

        // Determinar o movimento com base na posição da pastilha
        return determinarMovimento(posicaoPastilha);
    }

    // Função para determinar o movimento em direção a uma posição
    private int determinarMovimento(int posicao) {
        switch (posicao) {
            case 7:
            case 2:
                return 1; // Mover para cima
            case 17:
            case 22:
                return 2; // Mover para baixo
            case 11:
            case 6:
                return 4; // Mover para esquerda
            case 13:
            case 8:
                return 3; // Mover para direita
            default:
                return movimentoAleatorioSemParar(); // Movimento padrão caso não haja direção clara
        }
    }

    // Função para verificar se deve desviar de ladrão
    private boolean deveDesviarDeLadrao(int[] olfatoLadrao, int[] visao) {
        // Verifica no olfato
        for (int i = 0; i < olfatoLadrao.length; i++) {
            int intensidade = olfatoLadrao[i];
            if (intensidade > 0 && calcularDistancia(i, 12) <= 3) { // distância <= 3
                return true; // Fuga imediata
            }
        }
        // Verifica na visão (código 200 indica ladrão)
        return contains(visao, 200);
    }

    // Função para fugir dos ladrões, movendo 7 vezes na direção oposta
    private int fugirDeLadrao(int[] olfatoLadrao) {
        int[] direcoes = {1, 2, 3, 4}; // Cima, Baixo, Direita, Esquerda
        int[] posicoesVisao = {1, 7, 5, 3}; // Mapeia os índices de olfato para as direções

        // Encontrar a direção com a maior intensidade de ladrão
        int maiorIntensidade = -1;
        int piorMovimento = movimentoAleatorioSemParar(); // Movimento padrão caso todas direções tenham ladrões

        for (int i = 0; i < posicoesVisao.length; i++) {
            int intensidadeOlfato = olfatoLadrao[posicoesVisao[i]];
            if (intensidadeOlfato > maiorIntensidade) {
                maiorIntensidade = intensidadeOlfato;
                piorMovimento = direcoes[i]; // Mover para a direção com maior intensidade de ladrão
            }
        }

        // Retorna o movimento na direção oposta por 7 passos consecutivos
        return moverVariosPassos(oposto(piorMovimento), PASSOS_DE_FUGA);
    }

    // Função que retorna o movimento oposto
    private int oposto(int movimento) {
        switch (movimento) {
            case 1: return 2; // Cima -> Baixo
            case 2: return 1; // Baixo -> Cima
            case 3: return 4; // Direita -> Esquerda
            case 4: return 3; // Esquerda -> Direita
            default: return movimentoAleatorioSemParar(); // Se movimento inválido, movimento aleatório
        }
    }

    // Função para realizar múltiplos movimentos em uma direção
    private int moverVariosPassos(int direcao, int passos) {
        for (int i = 0; i < passos; i++) {
            // Realizar o movimento na mesma direção
            // Aqui você pode adicionar a lógica necessária para garantir que o poupador continue se movendo
        }
        return direcao; // Retorna o movimento na direção escolhida
    }

    private int moverParaMoeda(int[] visao) {
        int posicaoMoeda = -1;
        int menorDistancia = Integer.MAX_VALUE;

        // Procurar por moedas (valor 4) na visão
        for (int i = 0; i < visao.length; i++) {
            if (visao[i] == 4) { // Se encontrar moeda
                // Calcular a distância do poupador (posição 12) até a moeda
                int distancia = calcularDistancia(12, i); // Poupador está na posição 12

                // Verifica se é a moeda mais próxima
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    posicaoMoeda = i;
                }
            }
        }

        // Se nenhuma moeda for encontrada, fazer movimento aleatório
        if (posicaoMoeda == -1) {
            return movimentoAleatorioSemParar();
        }

        // Determinar o movimento com base na posição da moeda
        return decidirMovimentoParaMoeda(posicaoMoeda);
    }

    // Função para calcular a distância entre duas posições
    private int calcularDistancia(int posicaoPoupador, int posicaoAlvo) {
        int rowPoupador = posicaoPoupador / 5;
        int colPoupador = posicaoPoupador % 5;
        int rowAlvo = posicaoAlvo / 5;
        int colAlvo = posicaoAlvo % 5;

        return Math.abs(rowPoupador - rowAlvo) + Math.abs(colPoupador - colAlvo);
    }

    // Função para decidir o movimento baseado na posição da moeda
    private int decidirMovimentoParaMoeda(int posicaoMoeda) {
        if (posicaoMoeda == 7 || posicaoMoeda == 2) {
            return 1; // Mover para cima
        } else if (posicaoMoeda == 17 || posicaoMoeda == 22) {
            return 2; // Mover para baixo
        } else if (posicaoMoeda == 11 || posicaoMoeda == 6) {
            return 4; // Mover para esquerda
        } else if (posicaoMoeda == 13 || posicaoMoeda == 8) {
            return 3; // Mover para direita
        } else {
            return movimentoAleatorioSemParar();
        }
    }

    private int movimentoAleatorioSemParar() {
        return 1 + (int) (Math.random() * 4); // Movimento aleatório, excluindo parado
    }

    private boolean contains(int[] array, int valor) {
        for (int i : array) {
            if (i == valor) {
                return true;
            }
        }
        return false;
    }
}
