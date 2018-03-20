package br.com.whatsappandroid.cursoandroid.whatsapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.adapter.MensagemAdapter;
import br.com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Conversa;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Mensagem;

public class ConversaActivity extends AppCompatActivity {

    private EditText edit_mensage;
    private ImageButton bt_mensagem;

    private DatabaseReference firebase;


    private Toolbar toolbar;

    //dados do destinatário
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    //dados remetente
    private String idUsuarioRemetente;
    private String nomeUsuarioRemetente;

    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;

    private ListView listview;

    private ValueEventListener valueEventListenerMensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = findViewById(R.id.tb_conversa);
        edit_mensage = findViewById(R.id.edit_mensagem);
        bt_mensagem = findViewById(R.id.bt_enviar);

        listview = findViewById(R.id.lv_conversas);

        //recuperar dados do usuário logado
        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        idUsuarioRemetente = preferencias.getIdentificador();
        nomeUsuarioRemetente = preferencias.getNome();


        Bundle extra = getIntent().getExtras(); //passar dados entre um activity e outra

        if(extra != null){
            nomeUsuarioDestinatario = extra.getString("nome");
            String emailDestinatario = extra.getString("email");
            idUsuarioDestinatario = Base64Custom.codificarBase64(emailDestinatario);
        }

        //configurar toolbar
        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        //monta listview e adapter
        mensagens = new ArrayList<>();


        adapter = new MensagemAdapter(
          ConversaActivity.this,
                mensagens
        );

        listview.setAdapter(adapter);

        //recupera mensagens do firebase
        firebase = ConfiguracaoFirebase.getFirebase()
                .child("mensagens")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);



        //cria listener para mensagens
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                //limpar mensagens
                mensagens.clear();

                //recuperar mensagem
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    mensagens.add(mensagem);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerMensagem);


        //enviar mensagem
        bt_mensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textoMensagem = edit_mensage.getText().toString();

                if(textoMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity.this, "Digite uma mensagem para enviar", Toast.LENGTH_SHORT).show();
                } else {

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetente);
                    mensagem.setMensagem(textoMensagem);

                    //salva mensagem para remtente
                    Boolean retornoMensagemRemetente = salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                    if(!retornoMensagemRemetente) {
                        Toast.makeText(ConversaActivity.this,
                                "Problema ao salvar mensagem, tente novamente!",
                                Toast.LENGTH_LONG).show();
                    } else {

                        //salva mensagem para rdestinatario
                        Boolean retornoMensagemDestinatario = salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                        if(!retornoMensagemDestinatario) {
                            Toast.makeText(ConversaActivity.this,
                                    "Problema ao salvar mensagem, tente novamente!",
                                    Toast.LENGTH_LONG).show();
                        }

                    }

                    //salvar conversa para remetente
                    Conversa conversa = new Conversa();
                    conversa.setIdusuario(idUsuarioDestinatario);
                    conversa.setNome(nomeUsuarioDestinatario);
                    conversa.setMensagem(textoMensagem);
                    Boolean retornoConversaDestinatario = salvarConversa(idUsuarioRemetente, idUsuarioDestinatario, conversa);
                    if(!retornoConversaDestinatario){
                        Toast.makeText(ConversaActivity.this,
                                "Problema ao salvar conversa, tente novamente!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        //salvar conversa para o destinatario
                        Conversa conversaRemetente = new Conversa();
                        conversaRemetente.setIdusuario(idUsuarioRemetente);
                        conversaRemetente.setNome(nomeUsuarioRemetente);
                        conversaRemetente.setMensagem(textoMensagem);
                        Boolean retornoConversaRemetente = salvarConversa(idUsuarioDestinatario, idUsuarioRemetente, conversaRemetente);
                        if(!retornoConversaRemetente){
                            Toast.makeText(ConversaActivity.this,
                                    "Problema ao salvar conversa, tente novamente!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    edit_mensage.setText("");

                }
            }
        });

    }

    private boolean salvarConversa(String idRemetente, String idDestinatario, Conversa conversa) {
        try {
            firebase = ConfiguracaoFirebase.getFirebase().child("conversas");
            firebase.child( idRemetente)
                    .child(idDestinatario)
                    .setValue(conversa); //não usa o push para forçar a sobrescrita no banco (sempre mostrar a ultima mensagem
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }
    }

    private boolean salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem) {
        try{
            firebase = ConfiguracaoFirebase.getFirebase().child("mensagens");

            firebase.child(idRemetente)
                    .child(idDestinatario)
                    .push() //cria novo nó com id único - firebase gerencia isso xD
                    .setValue(mensagem);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMensagem);
    }
}
