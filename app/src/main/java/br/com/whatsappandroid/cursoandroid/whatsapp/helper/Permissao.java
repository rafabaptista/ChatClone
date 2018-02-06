package br.com.whatsappandroid.cursoandroid.whatsapp.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafael.baptista on 22/01/2018.
 */

public class Permissao {

    public static boolean validaPermissoes(int requestCode, Activity activity, String[] permissoes) {
        //algumas permissoes consideradas perigosas, eh necessario criar permissao para usuario - isso acima do SDK 23

        List<String> listaPermissoes = new ArrayList<String>();

        if(Build.VERSION.SDK_INT >= 23) {
            for(String permissao: permissoes){
                Boolean validaPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;

                if(!validaPermissao) listaPermissoes.add(permissao);
            }

            //caso a lista esteja vazia, não é necessário solicitar permissão
            if(listaPermissoes.isEmpty()) return true;

            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);

            //Solicita Permissão
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode );
        }

        return true;
    }
}
