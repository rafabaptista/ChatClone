package br.com.whatsappandroid.cursoandroid.whatsapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Contato;

/**
 * Created by rafael on 19/02/18.
 */

public class ContatoAdapter extends ArrayAdapter<Contato> {

    private ArrayList<Contato> contatos;
    private Context context;

    public ContatoAdapter(Context context, ArrayList<Contato> objects) {
        super(context, 0, objects);

        this.contatos = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //criar view do zero
        View view = null;

        //verifica se a lista está vazia
        if(contatos != null){
            //inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE); //interface global que permite inicializar classes e serviços

            //monta a view a partir do xml
            view = inflater.inflate(R.layout.lista_contatos, parent, false);

            //recupera elemento para exibição
            TextView nomecontato = view.findViewById(R.id.tv_nome);
            TextView emailContato = view.findViewById(R.id.tv_email);

            Contato contato = contatos.get(position);
            nomecontato.setText(contato.getNome());
            emailContato.setText(contato.getEmail());

        }

        return view;

    }
}
