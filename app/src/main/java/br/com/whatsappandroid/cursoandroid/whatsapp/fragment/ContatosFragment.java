package br.com.whatsappandroid.cursoandroid.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.activity.ConversaActivity;
import br.com.whatsappandroid.cursoandroid.whatsapp.adapter.ContatoAdapter;
import br.com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Contato;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    ArrayList<Contato> contatos;

    private DatabaseReference firebase;

    private ValueEventListener valueEventListenerContatos;

    public ContatosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart(); //é chamado quando o fragment é iniciado

        firebase.addValueEventListener(valueEventListenerContatos);

    }

    @Override
    public void onStop() {
        super.onStop();

        firebase.removeEventListener(valueEventListenerContatos);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        //instanciar ojetos
        contatos = new ArrayList<>();


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //monta listView e adapter
        listView = view.findViewById(R.id.lv_Contatos);
//        adapter = new ArrayAdapter(
//                getActivity(), //pega a activity onde o fragment está
//                R.layout.lista_contatos,
//                contatos
//        );


        adapter = new ContatoAdapter(getActivity(), contatos);

        listView.setAdapter(adapter);


        //recuperar firebase
        Preferencias preferencias = new Preferencias(getActivity());
        String identificadorUsuarioLogado = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFirebase().child("contatos").child(identificadorUsuarioLogado);

        //listener para recuperar contatos
        valueEventListenerContatos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //limpar lista
                contatos.clear();

                //listar contatos
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Contato contato = dados.getValue(Contato.class); //recupera os valores e cria o objeto baseado nos valores
                    contatos.add(contato);

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), ConversaActivity.class);

                //recupera dados a serem passados
                Contato contato = contatos.get(position);

                //enviando dados para a conversa
                intent.putExtra("nome", contato.getNome() );
                intent.putExtra("email", contato.getEmail());

                startActivity(intent);
            }
        });



        return view;
    }

}
