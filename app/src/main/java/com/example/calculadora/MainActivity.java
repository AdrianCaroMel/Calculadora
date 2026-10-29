package com.example.calculadora;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView display;
    private final String operadores = "+−×÷";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Vincular el display
        display = findViewById(R.id.display);

        // 2. Array con los ids de todos los botones
        int[] idsBotones = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnComa, R.id.btnSumar, R.id.btnRestar,
                R.id.btnMultiplicar, R.id.btnDividir,
                R.id.btnIgual, R.id.btnC, R.id.btnAC
        };

        // 3. Poner el listener a cada botón
        for (int i = 0; i < idsBotones.length; i++) {
            Button boton = findViewById(idsBotones[i]);
            boton.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {

        // 1. Tecla pulsada y texto actual del display
        String tecla = ((Button) view).getText().toString();
        String texto = display.getText().toString();

        // 2. Número que se está escribiendo (lo que hay tras el último operador)
        int posUltimoOperador = -1;
        for (int i = 0; i < texto.length(); i++) {
            if (operadores.indexOf(texto.charAt(i)) >= 0) {
                posUltimoOperador = i;
            }
        }
        String numeroActual = texto.substring(posUltimoOperador + 1);

        // 3. Según la tecla pulsada
        if (tecla.equals("AC")) {

            // 3.1 AC: display a 0
            texto = "0";

        } else if (tecla.equals("C")) {

            // 3.2 C: borrar el último carácter
            texto = texto.substring(0, texto.length() - 1);
            if (texto.length() == 0) {
                texto = "0";
            }

        } else if (tecla.equals(",")) {

            // 3.3 Coma: solo una por número
            if (!numeroActual.contains(",")) {
                texto = texto + ",";
            }

        } else if (operadores.contains(tecla)) {

            // 3.4 Operador: se concatena al texto
            texto = texto + tecla;

        } else if (tecla.equals("=")) {

            // 3.5 Igual: separar números y operadores
            ArrayList<Double> numeros = new ArrayList<>();
            ArrayList<Character> ops = new ArrayList<>();
            String numero = "";
            boolean dividePorCero = false;

            for (int i = 0; i < texto.length(); i++) {
                char c = texto.charAt(i);
                if (operadores.indexOf(c) >= 0) {
                    numeros.add(Double.parseDouble(numero.replace(',', '.')));
                    ops.add(c);
                    numero = "";
                } else {
                    numero = numero + c;
                }
            }
            numeros.add(Double.parseDouble(numero.replace(',', '.')));

            // 3.6 Primero × y ÷
            int i = 0;
            while (i < ops.size() && !dividePorCero) {
                char op = ops.get(i);
                if (op == '×' || op == '÷') {
                    double a = numeros.get(i);
                    double b = numeros.get(i + 1);
                    if (op == '÷' && b == 0) {
                        dividePorCero = true;
                    } else {
                        if (op == '×') {
                            numeros.set(i, a * b);
                        } else {
                            numeros.set(i, a / b);
                        }
                        numeros.remove(i + 1);
                        ops.remove(i);
                    }
                } else {
                    i++;
                }
            }

            // 3.7 Después + y − de izquierda a derecha
            while (ops.size() > 0 && !dividePorCero) {
                double a = numeros.get(0);
                double b = numeros.get(1);
                if (ops.get(0) == '+') {
                    numeros.set(0, a + b);
                } else {
                    numeros.set(0, a - b);
                }
                numeros.remove(1);
                ops.remove(0);
            }

            // 3.8 Mostrar resultado o avisar de la división entre 0
            if (dividePorCero) {
                Toast.makeText(this, "No se puede dividir entre 0", Toast.LENGTH_SHORT).show();
                texto = "0";
            } else {
                double resultado = numeros.get(0);
                if (resultado == (long) resultado) {
                    texto = String.valueOf((long) resultado);
                } else {
                    texto = String.valueOf(resultado).replace('.', ',');
                }
            }

        } else {

            // 3.9 Número: sin ceros a la izquierda
            if (numeroActual.equals("0")) {
                texto = texto.substring(0, texto.length() - 1) + tecla;
            } else {
                texto = texto + tecla;
            }
        }

        // 4. Mostrar el texto en el display
        display.setText(texto);
    }
}