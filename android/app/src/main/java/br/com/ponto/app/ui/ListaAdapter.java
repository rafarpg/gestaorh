package br.com.ponto.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.ponto.app.databinding.ItemListaBinding;

public class ListaAdapter<T> extends RecyclerView.Adapter<ListaAdapter.ItemViewHolder> {

    public interface Preenchedor<T> {
        void preencher(ItemListaBinding item, T dado);
    }

    public interface AoClicar<T> {
        void clicou(T dado);
    }

    private final List<T> itens = new ArrayList<>();
    private final Preenchedor<T> preenchedor;
    private final AoClicar<T> aoClicar;

    public ListaAdapter(Preenchedor<T> preenchedor, AoClicar<T> aoClicar) {
        this.preenchedor = preenchedor;
        this.aoClicar = aoClicar;
    }

    public void atualizar(List<T> novosItens) {
        itens.clear();
        itens.addAll(novosItens);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(ItemListaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        T dado = itens.get(position);
        preenchedor.preencher(holder.item, dado);
        esconderSeVazio(holder.item.textoLinha1);
        esconderSeVazio(holder.item.textoLinha2);
        if (aoClicar != null) {
            holder.itemView.setOnClickListener(v -> aoClicar.clicou(dado));
        }
    }

    private static void esconderSeVazio(TextView texto) {
        texto.setVisibility(texto.getText().length() == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        final ItemListaBinding item;

        ItemViewHolder(ItemListaBinding item) {
            super(item.getRoot());
            this.item = item;
        }
    }
}
