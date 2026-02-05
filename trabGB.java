import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class trabGB {

    // MATRIZ DO ESTACIONAMENTO (Formato: "tipo:hora:minuto")
    public static String[][] estacionamento;

    // VARIÁVEIS DE CONFIGURAÇÃO E CÁLCULO
    public static double precoInicialBase; // Valor dos primeiros 30 minutos.
    public static double precoAdicionalBase; // Valor dos 30 minutos adicionais.
    public static int corredores;
    public static int vagas;
    public static int totalVagas;

    // CONTROLE DE VEÍCULOS
    public static double totalMoto = 0;
    public static double totalCarro = 0;
    public static double totalVan = 0;
    public static int qtdMoto = 0;
    public static int qtdCarro = 0;
    public static int qtdVan = 0;

    // OPÇÃO 1 — Carregar dados
    public static void funArquivo(Scanner scanner, String nomeArquivo) {

        try {
            BufferedReader br = new BufferedReader(new FileReader(nomeArquivo));
            String linhaLida;
            int contadorVeiculos = 0;

            // Limpa o estacionamento antes de carregar novos dados.
            if (estacionamento != null) {
                for (int i = 0; i < corredores; i++) {
                    for (int j = 0; j < vagas; j++) {
                        estacionamento[i][j] = null;
                    }
                }
            }

            // Lê e ignora o cabeçalho, caso ele exista.
            linhaLida = br.readLine();

            if (linhaLida != null && linhaLida.trim().toUpperCase().startsWith("VAGA=TIPO")) {
                System.out.println("Cabeçalho de arquivo detectado e ignorado.");
            } else if (linhaLida != null) {
                // Caso não haja cabeçalho, processa como a primeira linha de dados.
                try {
                    if (linhaLida.contains("=")) {
                        String[] partes = linhaLida.split("=");
                        if (partes.length >= 2) {
                            String pos = partes[0].trim().toUpperCase();
                            String conteudo = partes[1].trim();

                            // Converte a posição da vaga para índices da matriz
                            int linha = -1;
                            int coluna = -1;

                            if (pos.length() >= 2) {
                                linha = pos.charAt(0) - 'A';
                                coluna = Integer.parseInt(pos.substring(1)) - 1;
                            }

                            // Verifica se a posição é válida dentro dos limites atuais
                            if (linha >= 0 && linha < corredores && coluna >= 0 && coluna < vagas) {

                                // Processa o conteúdo
                                String[] dadosVaga = conteudo.split(":");
                                if (dadosVaga.length == 3) {
                                    String tipo = dadosVaga[0];

                                    if (tipo.equals("M") || tipo.equals("C") || tipo.equals("V")) {
                                        int h = Integer.parseInt(dadosVaga[1]); // Pode lançar NumberFormatException
                                        int m = Integer.parseInt(dadosVaga[2]); // Pode lançar NumberFormatException

                                        if (h >= 0 && h <= 23 && m >= 0 && m <= 59) {
                                            estacionamento[linha][coluna] = conteudo;
                                            contadorVeiculos++; // Linha processada com sucesso
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception formatError) {
                    // Ignora a primeira linha caso haja erro de formato.
                }
            }

            // Realiza a leitura das linhas restantes.
            while ((linhaLida = br.readLine()) != null) {

                try {

                    if (!linhaLida.contains("="))
                        continue; // Pula linha sem delimitador.

                    String[] partes = linhaLida.split("=");
                    if (partes.length < 2)
                        continue; // Pula linha sem conteúdo após o '='.

                    String pos = partes[0].trim().toUpperCase();
                    String conteudo = partes[1].trim();

                    int linha = -1;
                    int coluna = -1;

                    if (pos.length() >= 2) {
                        linha = pos.charAt(0) - 'A';
                        coluna = Integer.parseInt(pos.substring(1)) - 1;
                    } else {
                        continue; // Pula vagas mal formatadas.
                    }

                    // Verifica se a posição é válida dentro dos limites atuais.
                    if (linha < 0 || linha >= corredores || coluna < 0 || coluna >= vagas) {
                        continue;
                    }

                    // Processa o conteúdo.
                    String[] dadosVaga = conteudo.split(":");
                    if (dadosVaga.length == 3) {
                        String tipo = dadosVaga[0];

                        if (tipo.equals("M") || tipo.equals("C") || tipo.equals("V")) {
                            int h = Integer.parseInt(dadosVaga[1]);
                            int m = Integer.parseInt(dadosVaga[2]);

                            if (h >= 0 && h <= 23 && m >= 0 && m <= 59) {
                                estacionamento[linha][coluna] = conteudo;
                                contadorVeiculos++;
                            }
                        }
                    }

                } catch (Exception formatError) {
                    // Ignora a linha com erro de formato (ex: hora não é número) e continua.
                    continue;
                }
            }

            br.close();
            System.out.println("Arquivo carregado com sucesso! (" + contadorVeiculos + " veículos registrados)");

        } catch (Exception e) {
            // Mensagem de erro caso ocorra algum problema com o arquivo.
            System.out.println("ERRO: Ocorreu um problema ao tentar carregar o arquivo '" + nomeArquivo
                    + "'. Detalhe: " + e.getMessage());
            System.out.println("O estacionamento será inicializado como vazio.");

            // Garante que o estacionamento esteja limpo após o erro.
            if (estacionamento != null) {
                for (int i = 0; i < corredores; i++) {
                    for (int j = 0; j < vagas; j++) {
                        estacionamento[i][j] = null;
                    }
                }
            }
        }
    }

    // OPÇÃO 2 — Consultar vaga
    public static void funConsulta(Scanner entrada) {
        System.out.print("Informe a vaga (ex: A1): ");
        String vagaStr = entrada.nextLine().trim().toUpperCase();

        // Conversão de vaga (ex: "A1") para índices [linha][coluna]
        int linha = -1, coluna = -1;
        try {
            if (vagaStr.length() >= 2) {
                linha = vagaStr.charAt(0) - 'A';
                coluna = Integer.parseInt(vagaStr.substring(1)) - 1;
            }
        } catch (Exception e) {
        }

        if (linha < 0 || linha >= corredores || coluna < 0 || coluna >= vagas) {
            System.out.println("Vaga inválida ou fora dos limites do estacionamento.");
            return;
        }

        String dado = estacionamento[linha][coluna];

        if (dado == null) {
            System.out.println("Vaga livre.");
        } else {
            String[] p = dado.split(":");
            System.out.println("--- Dados da Vaga " + vagaStr + " ---");

            String tipoExtenso = "";
            switch (p[0]) {
                case "M":
                    tipoExtenso = "Moto";
                    break;
                case "C":
                    tipoExtenso = "Carro";
                    break;
                case "V":
                    tipoExtenso = "Van";
                    break;
            }

            System.out.println("Tipo: " + tipoExtenso + " (" + p[0] + ")");
            String hEntradaFormatada = String.format("%02d", Integer.parseInt(p[1]));
            String mEntradaFormatada = String.format("%02d", Integer.parseInt(p[2]));
            System.out.println("Entrada: " + hEntradaFormatada + ":" + mEntradaFormatada);
        }
    }

    // OPÇÃO 3 — Entrada de veículo
    public static void funEntrada(Scanner entrada) {
        // Coleta dados do veículo e realiza a validação.
        String tipo;
        do {
            System.out.print("Tipo de veículo (Moto/Carro/Van): ");
            tipo = entrada.nextLine().trim().toUpperCase();
            if (!tipo.equals("M") && !tipo.equals("C") && !tipo.equals("V")) {
                System.out.println("Tipo de veículo inválido. Utilize M (Moto), C (Carro) ou V (Van).");
            }
        } while (!tipo.equals("M") && !tipo.equals("C") && !tipo.equals("V"));

        int h = -1, m = -1;
        while (h < 0 || h > 23 || m < 0 || m > 59) {
            System.out.print("Hora de entrada (0-23): ");
            if (entrada.hasNextInt())
                h = entrada.nextInt();
            else
                entrada.next();
            System.out.print("Minuto de entrada (0-59): ");
            if (entrada.hasNextInt())
                m = entrada.nextInt();
            else
                entrada.next();
            entrada.nextLine();

            if (h < 0 || h > 23 || m < 0 || m > 59) {
                System.out.println("Horário de entrada inválido. Tente novamente.");
            }
        }

        // Realiza a busca da vaga
        System.out.print("Vaga (ex: A1), Corredor (ex: A), ou ENTER para primeira livre: ");
        String localizacao = entrada.nextLine().trim().toUpperCase();

        int linhaEncontrada = -1;
        int colunaEncontrada = -1;

        // Armazena os dados em TIPO:HORA:MINUTO.
        String novoRegistro = tipo + ":" + h + ":" + m;

        if (localizacao.length() >= 2) {
            // Caso seja informada a vaga exata.
            int l = -1, c = -1;
            try {
                l = localizacao.charAt(0) - 'A';
                c = Integer.parseInt(localizacao.substring(1)) - 1;
            } catch (Exception e) {
                System.out.println("AVISO: Formato de vaga inválido.");
                return;
            }

            if (l >= 0 && l < corredores && c >= 0 && c < vagas) {
                if (estacionamento[l][c] == null) {
                    linhaEncontrada = l;
                    colunaEncontrada = c;
                    System.out.println("Vaga " + localizacao + "livre.");
                } else {
                    System.out.println("AVISO: A vaga " + localizacao + " já está ocupada.");
                    return;
                }
            } else {
                System.out.println("AVISO: Vaga fora dos limites.");
                return;
            }

        } else if (localizacao.length() == 1 && localizacao.charAt(0) >= 'A'
                && localizacao.charAt(0) <= (char) ('A' + corredores - 1)) {
            // Caso Somente a letra do corredor seja informada.
            int linhaDesejada = localizacao.charAt(0) - 'A';

            boolean achou = false;
            for (int c = 0; c < vagas; c++) {
                if (estacionamento[linhaDesejada][c] == null) {
                    linhaEncontrada = linhaDesejada;
                    colunaEncontrada = c;
                    achou = true;
                    break;
                }
            }
            if (!achou) {
                System.out.println("Não há vagas livres no corredor " + localizacao.charAt(0) + ".");
                return;
            }

        } else if (localizacao.isEmpty()) {
            // Caso nenhuma vaga/corredor seja informado.
            boolean achou = false;
            for (int l = 0; l < corredores; l++) {
                for (int c = 0; c < vagas; c++) {
                    if (estacionamento[l][c] == null) {
                        linhaEncontrada = l;
                        colunaEncontrada = c;
                        achou = true;
                        break;
                    }
                }
                if (achou)
                    break;
            }
            if (!achou) {
                System.out.println("Não há vagas disponíveis no estacionamento.");
                return;
            }

        } else {
            System.out.println("AVISO: Entrada de vaga/corredor inválida. Use A1, A ou deixe vazio.");
            return;
        }

        // Registro
        if (linhaEncontrada != -1) {
            estacionamento[linhaEncontrada][colunaEncontrada] = novoRegistro;
            // Formata a vaga
            String vagaFormatada = "" + (char) ('A' + linhaEncontrada) + (colunaEncontrada + 1);
            System.out.println("Veículo estacionado com sucesso em: " + vagaFormatada);
        }
    }

    // OPÇÃO 4 — Saída de veículo
    public static void funSaida(Scanner entrada) {
        System.out.print("Informe a vaga (ex: B3) para registrar a saída: ");
        String vagaStr = entrada.nextLine().trim().toUpperCase();

        // Conversão de vaga para índices
        int l = -1, c = -1;
        try {
            if (vagaStr.length() >= 2) {
                l = vagaStr.charAt(0) - 'A';
                c = Integer.parseInt(vagaStr.substring(1)) - 1;
            }
        } catch (Exception e) {
            // Ignora
        }

        if (l < 0 || l >= corredores || c < 0 || c >= vagas) {
            System.out.println("Vaga inválida ou fora dos limites do estacionamento.");
            return;
        }

        if (estacionamento[l][c] == null) {
            System.out.println("A vaga " + vagaStr + " está livre.");
            return;
        }

        // Dados do veículo
        String[] p = estacionamento[l][c].split(":");
        String tipo = p[0];
        int hEntrada = Integer.parseInt(p[1]);
        int mEntrada = Integer.parseInt(p[2]);

        System.out.printf("Veículo estacionado (Tipo: %s, Entrada: %02d:%02d)\n", tipo, hEntrada, mEntrada);

        // Coleta o horário de saída
        int hSaida = -1, mSaida = -1;
        while (hSaida < 0 || hSaida > 23 || mSaida < 0 || mSaida > 59) {
            System.out.print("Hora de saída (0-23): ");
            if (entrada.hasNextInt())
                hSaida = entrada.nextInt();
            else
                entrada.next();
            System.out.print("Minuto de saída (0-59): ");
            if (entrada.hasNextInt())
                mSaida = entrada.nextInt();
            else
                entrada.next();
            entrada.nextLine(); // Limpa buffer

            if (hSaida < 0 || hSaida > 23 || mSaida < 0 || mSaida > 59) {
                System.out.println("Horário de saída inválido. Tente novamente.");
            }
        }

        // Cálculo da permanência em minutos
        int minEntradaTotal = hEntrada * 60 + mEntrada;
        int minSaidaTotal = hSaida * 60 + mSaida;

        int permanencia = minSaidaTotal - minEntradaTotal;

        if (permanencia <= 0) {
            System.out.println(
                    "ERRO: O horário de saída deve ser estritamente posterior ao de entrada. Operação cancelada.");
            return;
        }

        // Cálculo de Preço
        int blocos = (int) Math.ceil(permanencia / 30.0);

        // Determinação dos preços e multiplicadores
        double multiplicador;
        switch (tipo) {
            case "M":
                multiplicador = 0.70;
                break;
            case "C":
                multiplicador = 1.00;
                break;
            case "V":
                multiplicador = 1.30;
                break;
            default:
                multiplicador = 1.00;
        }

        double precoInicial = precoInicialBase * multiplicador;
        double precoAdicional = precoAdicionalBase * multiplicador;

        double valor = precoInicial;
        if (blocos > 1) {
            valor += (blocos - 1) * precoAdicional;
        }

        // Exibição dos resultados
        int hPermanencia = permanencia / 60;
        int mPermanencia = permanencia % 60;

        System.out.printf("\nPermanência: %02d:%02d (%d minutos)\n", hPermanencia, mPermanencia, permanencia);
        System.out.printf("Valor a pagar: R$ %.2f (Base: R$%.2f + %d valores adicionais de R$%.2f)\n",
                valor, precoInicial, (blocos > 1 ? blocos - 1 : 0), precoAdicional);

        // Confirmação de Liberação
        System.out.print("Deseja liberar a vaga " + vagaStr + "? (S/N): ");
        String confirmacao = entrada.nextLine().trim().toUpperCase();

        if (confirmacao.equals("S")) {
            // Atualiza controle financeiro
            switch (tipo) {
                case "M":
                    totalMoto += valor;
                    qtdMoto++;
                    break;
                case "C":
                    totalCarro += valor;
                    qtdCarro++;
                    break;
                case "V":
                    totalVan += valor;
                    qtdVan++;
                    break;
            }

            // Libera a vaga
            estacionamento[l][c] = null;
            System.out.println("Vaga liberada e valor registrado no financeiro.");
        } else {
            System.out.println("Vaga mantida ocupada. Operação de saída cancelada.");
        }
    }

    // OPÇÃO 5 — Ocupação
    public static void funOcupacao() {
        int vagasOcupadas = 0;
        int motoOcupada = 0;
        int carroOcupado = 0;
        int vanOcupada = 0;

        // Contagem
        for (int i = 0; i < corredores; i++) {
            for (int j = 0; j < vagas; j++) {
                if (estacionamento[i][j] != null) {
                    vagasOcupadas++;
                    // Busca o tipo de veículo
                    String tipo = estacionamento[i][j].substring(0, 1);
                    switch (tipo) {
                        case "M":
                            motoOcupada++;
                            break;
                        case "C":
                            carroOcupado++;
                            break;
                        case "V":
                            vanOcupada++;
                            break;
                    }
                }
            }
        }

        int vagasLivres = totalVagas - vagasOcupadas;

        System.out.println("\n===== MAPA DE OCUPAÇÃO =====");

        // Imprime cabeçalho das colunas (números das vagas)
        System.out.print("   ");
        for (int j = 0; j < vagas; j++) {
            System.out.printf("%-2d", (j + 1));
        }
        System.out.println();
        System.out.print("---");
        for (int j = 0; j < vagas; j++) {
            System.out.print("--");
        }
        System.out.println();

        // Imprime as linhas (corredores)
        for (int i = 0; i < corredores; i++) {
            System.out.print((char) ('A' + i) + " |");
            for (int j = 0; j < vagas; j++) {
                if (estacionamento[i][j] == null) {
                    System.out.print(" ."); // Vaga livre
                } else {
                    // Pega apenas a primeira letra (Tipo: M, C ou V)
                    System.out.print(" " + estacionamento[i][j].substring(0, 1));
                }
            }
            System.out.println();
        }

        // Resumo e Gráfico de Barras
        System.out.println("\n===== RESUMO DA OCUPAÇÃO =====");
        int tamanhoBarra = 20;
        int totalReferencia = (vagasOcupadas > 0) ? vagasOcupadas : 1;

        // Geração da barra para Moto
        int preenchido = (int) Math.ceil((double) motoOcupada / totalReferencia * tamanhoBarra);
        StringBuilder barraMoto = new StringBuilder();
        for (int i = 0; i < tamanhoBarra; i++) {
            barraMoto.append(i < preenchido ? "=" : " ");
        }
        System.out.printf("Moto   : %3d - %5.1f%% |%s| (%d vagas de %d)\n",
                motoOcupada, (double) motoOcupada / totalReferencia * 100,
                barraMoto.toString(), motoOcupada, vagasOcupadas);

        // Geração da barra para Carro
        preenchido = (int) Math.ceil((double) carroOcupado / totalReferencia * tamanhoBarra);
        StringBuilder barraCarro = new StringBuilder();
        for (int i = 0; i < tamanhoBarra; i++) {
            barraCarro.append(i < preenchido ? "=" : " ");
        }
        System.out.printf("Carro  : %3d - %5.1f%% |%s| (%d vagas de %d)\n",
                carroOcupado, (double) carroOcupado / totalReferencia * 100,
                barraCarro.toString(), carroOcupado, vagasOcupadas);

        // Geração da barra para Van
        preenchido = (int) Math.ceil((double) vanOcupada / totalReferencia * tamanhoBarra);
        StringBuilder barraVan = new StringBuilder();
        for (int i = 0; i < tamanhoBarra; i++) {
            barraVan.append(i < preenchido ? "=" : " ");
        }
        System.out.printf("Van    : %3d - %5.1f%% |%s| (%d vagas de %d)\n",
                vanOcupada, (double) vanOcupada / totalReferencia * 100,
                barraVan.toString(), vanOcupada, vagasOcupadas);

        // Exibe o resumo geral (baseado no total de vagas)
        System.out.println("\n--- Ocupação Geral (Total Vagas: " + totalVagas + ") ---");

        totalReferencia = totalVagas; // Ocupação geral usa o total de vagas como referência

        // Geração da barra para Ocupadas
        preenchido = (int) Math.ceil((double) vagasOcupadas / totalReferencia * tamanhoBarra);
        StringBuilder barraOcupadas = new StringBuilder();
        for (int i = 0; i < tamanhoBarra; i++) {
            barraOcupadas.append(i < preenchido ? "=" : " ");
        }
        System.out.printf("Ocupadas: %3d - %5.1f%% |%s| (%d vagas de %d)\n",
                vagasOcupadas, (double) vagasOcupadas / totalReferencia * 100,
                barraOcupadas.toString(), vagasOcupadas, totalVagas);

        // Geração da barra para Livres
        preenchido = (int) Math.ceil((double) vagasLivres / totalReferencia * tamanhoBarra);
        StringBuilder barraLivres = new StringBuilder();
        for (int i = 0; i < tamanhoBarra; i++) {
            barraLivres.append(i < preenchido ? "=" : " ");
        }
        System.out.printf("Livres  : %3d - %5.1f%% |%s| (%d vagas de %d)\n",
                vagasLivres, (double) vagasLivres / totalReferencia * 100,
                barraLivres.toString(), vagasLivres, totalVagas);
    }

    // OPÇÃO 6 — Financeiro
    public static void funFinanceiro() {
        double totalGeral = totalMoto + totalCarro + totalVan;
        int qtdGeral = qtdMoto + qtdCarro + qtdVan;

        System.out.println("\n===== RELATÓRIO FINANCEIRO (Veículos que Saíram) =====");
        System.out.println("---------------------------------");
        System.out.println("VEÍCULO | QUANTIDADE | VALOR (R$)");
        System.out.println("---------------------------------");
        System.out.printf("Moto    | %10d | %10.2f\n", qtdMoto, totalMoto);
        System.out.printf("Carro   | %10d | %10.2f\n", qtdCarro, totalCarro);
        System.out.printf("Van     | %10d | %10.2f\n", qtdVan, totalVan);
        System.out.println("---------------------------------");
        System.out.printf("Total   | %10d | %10.2f\n", qtdGeral, totalGeral);
        System.out.println("---------------------------------");
    }

    // OPÇÃO 7 — Salvar arquivo
    public static void funSalvar(Scanner entrada) {
        System.out.print("Nome do arquivo para salvar (ex: registros.txt): ");
        String nome = entrada.nextLine().trim();

        if (nome.isEmpty()) {
            nome = "registros_estacionamento.txt";
            System.out.println("Usando nome padrão: " + nome);
        } else if (!nome.toLowerCase().endsWith(".txt")) {
            nome += ".txt";
        }

        try {
            // Sobrescreve se o arquivo já existir.
            BufferedWriter bw = new BufferedWriter(new FileWriter(nome));

            // Adiciona o cabeçalho
            bw.write("Vaga=Tipo:Hora:Minuto");
            bw.newLine();

            int countSaved = 0;
            for (int i = 0; i < corredores; i++) {
                for (int j = 0; j < vagas; j++) {
                    if (estacionamento[i][j] != null) {
                        String vagaFormatada = "" + (char) ('A' + i) + (j + 1);
                        bw.write(vagaFormatada + "=" + estacionamento[i][j]);
                        bw.newLine();
                        countSaved++;
                    }
                }
            }

            bw.close();
            System.out.println("Dados do estacionamento salvos com sucesso no arquivo: " + nome + " (" + countSaved
                    + " veículos)");
        } catch (Exception e) {
            System.out.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    // Menu
    public static int funMenu(Scanner scanner) {
        System.out.println("\n===== SISTEMA DE ESTACIONAMENTO UNISINOS =====");
        System.out.println("1 - Carregar Dados");
        System.out.println("2 - Consultar Vaga");
        System.out.println("3 - Registrar Entrada");
        System.out.println("4 - Registrar Saída");
        System.out.println("5 - Ocupação (Mapa e Resumo)");
        System.out.println("6 - Financeiro (Relatório)");
        System.out.println("7 - Salvar Dados");
        System.out.println("8 - Integrantes");
        System.out.println("9 - Sair");
        System.out.print("Escolha uma opção: ");

        int item = -1;
        try {
            item = scanner.nextInt();
        } catch (java.util.InputMismatchException e) {
            item = -1;
        }
        scanner.nextLine();
        return item;
    }

    // OPÇÃO 8 — Integrantes
    public static void funIntegrantes() {
        System.out.println("\n===== CRÉDITOS =====");
        System.out.println("- Arthur Ferreira da Silva");
        System.out.println("- Kauan Rodrigues de Moraes");
        System.out.println("- Pedro Henrique Bortoli");
    }

    // Main
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String arquivoDefault = "teste.txt";

        System.out.println("===== CONFIGURAÇÃO INICIAL DO ESTACIONAMENTO =====");

        // Configuração de Preços
        System.out.print("Informe o valor inicial (30 min iniciais): ");
        while (!scanner.hasNextDouble() || (precoInicialBase = scanner.nextDouble()) < 0) {
            System.out.print("Valor inválido, tente novamente: ");
            scanner.nextLine();
        }

        System.out.print("Informe o valor adicional (a cada 30 min): ");
        while (!scanner.hasNextDouble() || (precoAdicionalBase = scanner.nextDouble()) < 0) {
            System.out.print("Valor inválido, tente novamente: ");
            scanner.nextLine();
        }

        // Exibição da tabela de preços
        double pIM = precoInicialBase * 0.70;
        double pIC = precoInicialBase * 1.00;
        double pIV = precoInicialBase * 1.30;
        double pAM = precoAdicionalBase * 0.70;
        double pAC = precoAdicionalBase * 1.00;
        double pAV = precoAdicionalBase * 1.30;

        System.out.println("\n--- Tabela de Preços ---");
        System.out.printf("Moto (70%%) - Inicial: R$%.2f / Adicional: R$%.2f\n", pIM, pAM);
        System.out.printf("Carro (100%%) - Inicial: R$%.2f / Adicional: R$%.2f\n", pIC, pAC);
        System.out.printf("Van (130%%) - Inicial: R$%.2f / Adicional: R$%.2f\n", pIV, pAV);
        scanner.nextLine();

        // Configuração de Tamanho do Estacionamento
        System.out.println("\n--- Configuração do Estacionamento ---");
        System.out.print("Informe a quantidade de corredores (entre 5 e 15): ");
        while (!scanner.hasNextInt() || (corredores = scanner.nextInt()) < 5 || corredores > 15) {
            System.out.print("Valor inválido, tente novamente: ");
            scanner.nextLine();
        }

        System.out.print("Informe a quantidade de vagas por corredor (entre 5 e 20): ");
        while (!scanner.hasNextInt() || (vagas = scanner.nextInt()) < 5 || vagas > 20) {
            System.out.print("Valor inválido, tente novamente: ");
            scanner.nextLine();
        }
        scanner.nextLine();

        totalVagas = corredores * vagas;
        estacionamento = new String[corredores][vagas];

        // Carregamento Inicial
        System.out.println("\n--- Carregamento de Dados ---");
        System.out.print(
                "Informe o nome do arquivo de registros (ENTER para usar '" + arquivoDefault + "'): ");
        String arquivo = scanner.nextLine().trim();
        if (arquivo.isEmpty()) {
            arquivo = arquivoDefault;
        } else if (!arquivo.toLowerCase().endsWith(".txt")) {
            arquivo += ".txt";
        }

        funArquivo(scanner, arquivo); // Carrega os dados
        String ultimoArquivoUsado = arquivo;

        // Menu
        int escolha = 0;
        while (escolha != 9) {
            escolha = funMenu(scanner);
            System.out.println("---------------------------------");

            try {
                switch (escolha) {
                    case 1:
                        System.out.print(
                                "Nome do arquivo para carregar (ENTER para usar '" + ultimoArquivoUsado + "'): ");
                        String novoArquivo = scanner.nextLine().trim();
                        if (novoArquivo.isEmpty()) {
                            funArquivo(scanner, ultimoArquivoUsado);
                        } else {
                            if (!novoArquivo.toLowerCase().endsWith(".txt")) {
                                novoArquivo += ".txt";
                            }
                            funArquivo(scanner, novoArquivo);
                            ultimoArquivoUsado = novoArquivo;
                        }
                        break;
                    case 2:
                        funConsulta(scanner);
                        break;
                    case 3:
                        funEntrada(scanner);
                        break;
                    case 4:
                        funSaida(scanner);
                        break;
                    case 5:
                        funOcupacao();
                        break;
                    case 6:
                        funFinanceiro();
                        break;
                    case 7:
                        funSalvar(scanner);
                        break;
                    case 8:
                        funIntegrantes();
                        break;
                    case 9:
                        System.out.println("\nSaindo do programa. Obrigado por usar o Sistema de Estacionamento!");
                        break;
                    default:
                        System.out.println("\nOpção desconhecida!");
                        break;
                }
            } catch (Exception e) {
                System.out.println("\nERRO CRÍTICO: Ocorreu um problema. Retornando ao menu principal. Detalhe: "
                        + e.getMessage());
            }

            if (escolha != 9) {
                System.out.println("---------------------------------");
                System.out.print("Pressione ENTER para retornar.");
                scanner.nextLine();
            }
        }
        scanner.close();
    }
}