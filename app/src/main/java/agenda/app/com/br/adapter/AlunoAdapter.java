package agenda.app.com.br.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import agenda.app.com.br.R;
import java.util.List;

import agenda.app.com.br.modelo.Aluno;

/**
 * Created by massao on 23/02/17.
 */

public class AlunoAdapter extends BaseAdapter {

    private Context context;
    private List<Aluno> alunos;

    public AlunoAdapter(Context context, List<Aluno> alunos) {
        this.context = context;
        this.alunos = alunos;
    }

    @Override
    public int getCount() {
        return alunos.size();
    }

    @Override
    public Object getItem(int position) {
        return alunos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Aluno aluno = alunos.get(position);

        LayoutInflater inflater = LayoutInflater.from(context);

        //ConvertView é para reutilizar a views, pois o android carrega sempre mais um pouco antes
        //e depois as view, se ela já esta carregada, não preciso inflar denovo o layout
        View view = convertView;
        if (view == null) {
            //parent é o pai do layout, no caso a ListView
            //o false é para adicionar 2 vezes o mesmo layout em um item da lista
            view = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView campoNome = (TextView) view.findViewById(R.id.item_nome);
        campoNome.setText(aluno.getNome());

        TextView campoTelefone = (TextView) view.findViewById(R.id.item_telefone);
        campoTelefone.setText(aluno.getTelefone());

        ImageView campoFoto = (ImageView) view.findViewById(R.id.item_foto);
        String caminhoFoto = aluno.getCaminhoFoto();
        if (caminhoFoto != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(caminhoFoto);
            Bitmap bitmapReduzido = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
            campoFoto.setImageBitmap(bitmapReduzido);
            campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
            campoFoto.setTag(caminhoFoto);
        }

        TextView campoEndereco = (TextView) view.findViewById(R.id.item_endereco);

        if (campoEndereco != null)
            campoEndereco.setText(aluno.getEndereco());

        TextView campoSite = (TextView) view.findViewById(R.id.item_size);
        if (campoSite != null)
            campoSite.setText(aluno.getSite());

        return view;
    }
}
