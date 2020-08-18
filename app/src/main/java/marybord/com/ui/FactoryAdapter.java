package marybord.com.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import marybord.com.R;
import marybord.com.persistence.Factory;

public class FactoryAdapter extends ListAdapter<Factory, FactoryAdapter.FactoryHolder> {
    private OnItemClickListener listener;

    public FactoryAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Factory> DIFF_CALLBACK = new DiffUtil.ItemCallback<Factory>() {
        @Override
        public boolean areItemsTheSame(@NonNull Factory oldItem, @NonNull Factory newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Factory oldItem, @NonNull Factory newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getAddress().equals(newItem.getAddress()) &&
                    oldItem.getLinesNumber() == newItem.getLinesNumber();
        }
    };

    @NonNull
    @Override
    public FactoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.factory_item, parent, false);
        return new FactoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FactoryHolder holder, int position) {
        Factory currentFactory = getItem(position);
        holder.textViewName.setText(currentFactory.getName());
        holder.textViewAddress.setText(currentFactory.getAddress());
        String linesNumber = String.format("Production lines number: %s", currentFactory.getLinesNumber());
        holder.textViewLinesNumber.setText(linesNumber);
    }

    public Factory getFactoryAt(int position) {
        return getItem(position);
    }

    class FactoryHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewAddress;
        private TextView textViewLinesNumber;

        public FactoryHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewAddress = itemView.findViewById(R.id.text_view_address);
            textViewLinesNumber = itemView.findViewById(R.id.text_view_lines_number);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Factory factory);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}