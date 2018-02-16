package br.com.whatsappandroid.cursoandroid.whatsapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.HashMap;
import java.util.Random;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Permissao;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;

public class LoginActivity extends AppCompatActivity {

    private EditText paisCodigo;
    private EditText ddd;
    private EditText telefone;
    private EditText nome;

    private Button botaoCadastrar;

    //PERMISSOES A PARTIR DO SDK 23
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Permissao.validaPermissoes(1, this, permissoesNecessarias);

        telefone = findViewById(R.id.editTextTelefone);
        paisCodigo = findViewById(R.id.editTextCodigoPais);
        ddd = findViewById(R.id.editTextDdd);

        nome = findViewById(R.id.editTextNome);

        botaoCadastrar = findViewById(R.id.buttonCadastrarTelefone);

        //definir mascaras
        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("NNNNN-NNNN");
        SimpleMaskFormatter simpleMaskPaisCodigo = new SimpleMaskFormatter("+NN");
        SimpleMaskFormatter simpleMaskDdd = new SimpleMaskFormatter("NN");

        //declara mascara
        MaskTextWatcher maskTelefone = new MaskTextWatcher(telefone, simpleMaskTelefone);
        MaskTextWatcher maskPaisCodigo = new MaskTextWatcher(paisCodigo, simpleMaskPaisCodigo);
        MaskTextWatcher maskDdd = new MaskTextWatcher(ddd, simpleMaskDdd);

        //Aplica mascara
        telefone.addTextChangedListener(maskTelefone); //responsável por formatar
        paisCodigo.addTextChangedListener(maskPaisCodigo);
        ddd.addTextChangedListener(maskDdd);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nomeUsuario = nome.getText().toString();
                String telefoneCompleto =
                        (paisCodigo.getText().toString() +
                        ddd.getText().toString() +
                        telefone.getText().toString()).replace("+", "").replace("-", "");

                //gerar Token
                Random randomico = new Random();
                int intToken = randomico.nextInt(9999 - 100) + 1000;
                String token = String.valueOf(intToken);
                String mensagemEnvio = "WhatsApp Código de Confirmação: " + token + " ";

                Log.i("Token", "T: " + token);

                //Salvar dados para validação
                Preferencias preferencias = new Preferencias(getApplicationContext());
                //preferencias.salvarUsuarioPreferencia(nomeUsuario, telefoneCompleto, token);

                //HashMap<String, String> usuario = preferencias.getDadosUsuario();

                //Log.i("TOKEN", "T: " + usuario.get("token") + " Nome: " + usuario.get("nome") + " Telefone: " + usuario.get("telefone"));

                //Envio de SMS - precisa do +
                boolean enviadoSMS = enviaSMS("+" + telefoneCompleto, mensagemEnvio);

                if(enviadoSMS){

                    Intent validacao = new Intent(LoginActivity.this, ValidadorActivity.class);
                    startActivity(validacao);
                    finish(); //destroi activity atual

                } else {

                    Toast.makeText(LoginActivity.this, "Problema ao enviar o SMS, tente novamente", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private boolean enviaSMS(String telefone, String msg){

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(telefone, "", msg, null, null);

            return true;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    //informa usuario que precisa permitir
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); //override

        for(int resultado : grantResults) {
            if(resultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o App, é necessário aceitar as permissões");
        builder.setPositiveButton("CONFIRMA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
