package com.luizalabs.stewie.core.external;

import java.util.Calendar;

public class CalculaLigacao {

    private static float DESCONTO = 0.5f;
    private static float CUSTO_LIGACAO = 0.4f;
    private static float CUSTO_LIGACAO_DESCONTO = 0.4f * DESCONTO;
    private static int HORA_INICIO_DESCONTO = 18;
    private static int HORA_FINAL_DESCONTO = 8;
    private static int HORAS_SEM_DESCONTO_POR_DIA = HORA_INICIO_DESCONTO - HORA_FINAL_DESCONTO;
    private static float DESCONTO_MAIS_UMA_HORA = 0.85f; // desconto de 15 por cento

    public static float calculaTarifa(int diai, int mesi, int anoi, int horai, int minutoi, int segundoi, int diaf, int mesf, int anof, int horaf, int minutof, int segundof) {
        if (diai == diaf && mesi == mesf && anoi == anof) { // mesmo dia, mes e ano
            if (horai < HORA_INICIO_DESCONTO && horai >= HORA_FINAL_DESCONTO) { // hora inicial fora do horario do desconto 8 < hora < 18
                if (horaf < HORA_INICIO_DESCONTO && horaf >= HORA_FINAL_DESCONTO) { // hora final fora do horario do desconto 8 < hora < 18

                    float minutosSemDesconto = calculaMinutos(horai, minutoi, segundoi, horaf, minutof, segundof);
                    float valor = minutosSemDesconto * CUSTO_LIGACAO;
                    if (minutosSemDesconto > 60) {
                        return valor * DESCONTO_MAIS_UMA_HORA;
                    } else {
                        return valor;
                    }

                } else { // hora final dentro do horario do desconto 18 < hora < 8

                    float minutosSemDesconto = calculaMinutos(horai, minutoi, segundoi, HORA_INICIO_DESCONTO-1, 59, 59);
                    float minutosComDesconto = calculaMinutos(HORA_INICIO_DESCONTO, 0, 0, horaf, minutof, segundof);
                    float valor = minutosSemDesconto * CUSTO_LIGACAO + minutosComDesconto * CUSTO_LIGACAO_DESCONTO;
                    if (minutosSemDesconto + minutosComDesconto > 60) {
                        return valor * DESCONTO_MAIS_UMA_HORA;
                    } else {
                        return valor;
                    }

                }
            } else { // hora inicial dentro do horario do desconto 18 < hora < 8
                if (horaf < HORA_INICIO_DESCONTO && horaf >= HORA_FINAL_DESCONTO) { // hora final fora do horario do desconto 8 < hora < 18
                    // só pode ter horário inicial antes das 8, por ser no mesmo dia

                    float minutosComDesconto = calculaMinutos(horai, minutoi, segundoi, HORA_FINAL_DESCONTO, 0, 0);
                    float minutosSemDesconto = calculaMinutos(HORA_FINAL_DESCONTO, 0, 0, horaf, minutof, segundof);
                    float valor = minutosSemDesconto * CUSTO_LIGACAO + minutosComDesconto * CUSTO_LIGACAO_DESCONTO;
                    if (minutosSemDesconto + minutosComDesconto > 60) {
                        return valor * DESCONTO_MAIS_UMA_HORA;
                    } else {
                        return valor;
                    }

                } else { // hora final dentro do horario do desconto 18 < hora < 8

                    if ((horaf - horai) < HORAS_SEM_DESCONTO_POR_DIA) { // não passa por minutos sem desconto

                        float minutosComDesconto = calculaMinutos(horai, minutoi, segundoi, horaf, minutof, segundof);
                        float valor = minutosComDesconto * CUSTO_LIGACAO_DESCONTO;
                        if (minutosComDesconto > 60) {
                            return valor * DESCONTO_MAIS_UMA_HORA;
                        } else {
                            return valor;
                        }

                    } else { // passa por minutos com desconto

                        float minutosComDesconto = calculaMinutos(horai, minutoi, segundoi, HORA_FINAL_DESCONTO, 0, 0);
                        float minutosSemDesconto = calculaMinutos(HORA_FINAL_DESCONTO, 0, 0, HORA_INICIO_DESCONTO-1, 59, 59);
                        minutosComDesconto += calculaMinutos(HORA_INICIO_DESCONTO, 0, 0, horaf, minutof, segundof);

                        float valor = minutosSemDesconto * CUSTO_LIGACAO + minutosComDesconto * CUSTO_LIGACAO_DESCONTO;
                        if (minutosSemDesconto + minutosComDesconto > 60) {
                            return valor * DESCONTO_MAIS_UMA_HORA;
                        } else {
                            return valor;
                        }

                    }
                }
            }
        } else { // ligacao comeca e termina em dias diferentes
            int diferencaDias = diferencaDias(diai, mesi, anoi, diaf, mesf, anof);
            float valorAcumulado = 0;
            if (diferencaDias > 1) {
                valorAcumulado = (diferencaDias - 1) * getPreçoDia();
            }

            if (horai < HORA_INICIO_DESCONTO && horai >= HORA_FINAL_DESCONTO) { // hora inicial fora do horario do desconto 8 < hora < 18
                float minutosSemDescontoDiaI = calculaMinutos(horai, minutoi, segundoi, HORA_INICIO_DESCONTO-1, 59, 0);
                float minutosComDescontoDiaI = calculaMinutos(HORA_INICIO_DESCONTO, 0, 0, 24, 0, 0);


                if (horaf < HORA_INICIO_DESCONTO && horaf >= HORA_FINAL_DESCONTO) { // hora final fora do horario do desconto 8 < hora < 18

                    float minutosComDescontoDiaF = calculaMinutos(0, 0, 0, HORA_FINAL_DESCONTO, 0, 0);
                    float minutosSemDescontoDiaF = calculaMinutos(HORA_FINAL_DESCONTO, 0, 0, horaf, minutof, segundof);

                    float valorTotalMinutosComDesconto = (minutosComDescontoDiaI + minutosComDescontoDiaF) * CUSTO_LIGACAO_DESCONTO;
                    float valorTotalMinutosSemDesconto = (minutosSemDescontoDiaI + minutosSemDescontoDiaF) * CUSTO_LIGACAO;

                    return (valorTotalMinutosComDesconto + valorTotalMinutosSemDesconto + valorAcumulado) * DESCONTO_MAIS_UMA_HORA;
                } else { // hora final dentro do horario do desconto 18 < hora < 8

                    if (horaf >= HORA_INICIO_DESCONTO) {
                        float minutosComDescontoDiaF = calculaMinutos(0, 0, 0, HORA_FINAL_DESCONTO, 0, 0);
                        float minutosSemDescontoDiaF = calculaMinutos(HORA_FINAL_DESCONTO, 0, 0, HORA_INICIO_DESCONTO-1, 59, 59);
                        minutosComDescontoDiaF += calculaMinutos(HORA_INICIO_DESCONTO, 0, 0, horaf, minutof, segundof);

                        float valorTotalMinutosComDesconto = (minutosComDescontoDiaI + minutosComDescontoDiaF) * CUSTO_LIGACAO_DESCONTO;
                        float valorTotalMinutosSemDesconto = (minutosSemDescontoDiaI + minutosSemDescontoDiaF) * CUSTO_LIGACAO;

                        return (valorTotalMinutosComDesconto + valorTotalMinutosSemDesconto + valorAcumulado) * DESCONTO_MAIS_UMA_HORA;
                    } else {
                        float minutosComDescontoDiaF = calculaMinutos(0, 0, 0, horaf, minutof, segundof);

                        float valorTotalMinutosComDesconto = (minutosComDescontoDiaI + minutosComDescontoDiaF) * CUSTO_LIGACAO_DESCONTO;
                        float valorTotalMinutosSemDesconto = (minutosSemDescontoDiaI) * CUSTO_LIGACAO;

                        return (valorTotalMinutosComDesconto + valorTotalMinutosSemDesconto + valorAcumulado) * DESCONTO_MAIS_UMA_HORA;
                    }
                }


            } else { // hora inicial dentro do horario do desconto 18 < hora < 8

                float minutosSemDescontoDiaI = 0;
                float minutosComDescontoDiaI = 0;
                if (horai >= HORA_INICIO_DESCONTO) {
                    minutosComDescontoDiaI = calculaMinutos(horai, minutoi, segundoi, 24, 0, 0);
                } else {
                    minutosComDescontoDiaI = calculaMinutos(horai, minutoi, segundoi, HORA_FINAL_DESCONTO, 0, 0);
                    minutosSemDescontoDiaI = calculaMinutos(HORA_FINAL_DESCONTO, 0, 0, HORA_INICIO_DESCONTO-1, 59, 59);
                    minutosComDescontoDiaI += calculaMinutos(HORA_INICIO_DESCONTO, 0, 0, 24, 0, 0);
                }

                if (horaf < HORA_INICIO_DESCONTO && horaf >= HORA_FINAL_DESCONTO) { // hora final fora do horario do desconto 8 < hora < 18

                    float minutosComDescontoDiaF = calculaMinutos(0, 0, 0, HORA_FINAL_DESCONTO, 0, 0);
                    float minutosSemDescontoDiaF = calculaMinutos(HORA_FINAL_DESCONTO, 0, 0, horaf, minutof, segundof);

                    float valorTotalMinutosComDesconto = (minutosComDescontoDiaI + minutosComDescontoDiaF) * CUSTO_LIGACAO_DESCONTO;
                    float valorTotalMinutosSemDesconto = (minutosSemDescontoDiaI + minutosSemDescontoDiaF) * CUSTO_LIGACAO;

                    return (valorTotalMinutosComDesconto + valorTotalMinutosSemDesconto + valorAcumulado) * DESCONTO_MAIS_UMA_HORA;
                } else { // hora final dentro do horario do desconto 18 < hora < 8

                    if (horaf >= HORA_INICIO_DESCONTO) {
                        float minutosComDescontoDiaF = calculaMinutos(0, 0, 0, HORA_FINAL_DESCONTO, 0, 0);
                        float minutosSemDescontoDiaF = calculaMinutos(HORA_FINAL_DESCONTO, 0, 0, HORA_INICIO_DESCONTO-1, 59, 59);
                        minutosComDescontoDiaF += calculaMinutos(HORA_INICIO_DESCONTO, 0, 0, horaf, minutof, segundof);

                        float valorTotalMinutosComDesconto = (minutosComDescontoDiaI + minutosComDescontoDiaF) * CUSTO_LIGACAO_DESCONTO;
                        float valorTotalMinutosSemDesconto = (minutosSemDescontoDiaI + minutosSemDescontoDiaF) * CUSTO_LIGACAO;

                        return (valorTotalMinutosComDesconto + valorTotalMinutosSemDesconto + valorAcumulado) * DESCONTO_MAIS_UMA_HORA;
                    } else {
                        float minutosComDescontoDiaF = calculaMinutos(0, 0, 0, horaf, minutof, segundof);

                        float valorTotalMinutosComDesconto = (minutosComDescontoDiaI + minutosComDescontoDiaF) * CUSTO_LIGACAO_DESCONTO;
                        float valorTotalMinutosSemDesconto = (minutosSemDescontoDiaI) * CUSTO_LIGACAO;

                        return (valorTotalMinutosComDesconto + valorTotalMinutosSemDesconto + valorAcumulado) * DESCONTO_MAIS_UMA_HORA;
                    }
                }
            }
        }
    }

    public static void main(String... args) {
        float preco = calculaTarifa(18, 3, 2016, 18, 0, 0, 18, 3, 2016, 18, 1, 00);
        System.out.println("Preço = 0,2: " + (preco == 0.2f));

        float preco2 = calculaTarifa(18, 3, 2016, 14, 0, 0, 18, 3, 2016, 14, 1, 00);
        System.out.println("Preço = 0,4: " + (preco2 == 0.4f));

        float preco3 = calculaTarifa(18, 3, 2016, 8, 0, 0, 18, 3, 2016, 18, 0, 00);
        System.out.println("Preço = 203.66: " + (preco3 == 203.66f));

        float preco4 = calculaTarifa(18, 3, 2016, 7, 59, 0, 18, 3, 2016, 18, 0, 00);
        System.out.println("Preço = 203.83: " + (preco4 == 203.83f));

        float preco5 = calculaTarifa(18, 3, 2016, 7, 59, 0, 18, 3, 2016, 18, 1, 00);
        System.out.println("Preço = 204: " + (preco5 == 204f));

        float preco6 = calculaTarifa(17, 3, 2016, 15, 05, 0, 18, 3, 2016, 15, 05, 00);
        System.out.println("Preço = 346,46002: " + (preco6 == 346.46002f));

        float preco7 = calculaTarifa(17, 3, 2016, 18, 05, 0, 18, 3, 2016, 15, 05, 00);
        System.out.println("Preço = 286.45: " + (preco7 == 286.45f));

        float preco8 = calculaTarifa(17, 3, 2016, 18, 05, 0, 18, 3, 2016, 7, 05, 00);
        System.out.println("Preço = 132.6: " + (preco8 == 132.6f));

        float preco9 = calculaTarifa(17, 3, 2016, 16, 05, 0, 18, 3, 2016, 19, 05, 00);
        System.out.println("Preço = 396.27002: " + (preco9 == 396.27002f));
        
        float preco10 = calculaTarifa(14, 3, 2016, 16, 05, 0, 18, 3, 2016, 19, 05, 00);
        System.out.println("Preço = 1559.07: " + (preco10 == 1559.07f));
    }


    private static int diferencaDias(int diai, int mesi, int anoi, int diaf, int mesf, int anof) {
        Calendar calendarI = Calendar.getInstance();
        calendarI.set(anoi, mesi-1, diai);
        Calendar calendarF = Calendar.getInstance();
        calendarF.set(anof, mesf-1, diaf);

        long dias = calendarF.getTime().getTime() - calendarI.getTime().getTime();
        return (int) (dias / (1000 * 60 * 60 * 24));
    }

    /**
     * Retorna o valor de um dia inteiro de ligação
     * @return o valor de um dia inteiro de ligação
     */
    private static float getPreçoDia() {
        return 600 * CUSTO_LIGACAO_DESCONTO + 840 * CUSTO_LIGACAO;
    }

    private static float calculaMinutos(int horai, int minutoi, int segundoi, int horaf, int minutof, int segundof) {
        Calendar calendarI = Calendar.getInstance();
        calendarI.set(Calendar.HOUR_OF_DAY, horai);
        calendarI.set(Calendar.MINUTE, minutoi);
        calendarI.set(Calendar.SECOND, segundoi);
        Calendar calendarF = Calendar.getInstance();
        calendarF.set(Calendar.HOUR_OF_DAY, horaf);
        calendarF.set(Calendar.MINUTE, minutof);
        calendarF.set(Calendar.SECOND, segundof);

        long minutos = calendarF.getTime().getTime() - calendarI.getTime().getTime();
        return (int) (minutos / (1000 * 60));
    }
}
