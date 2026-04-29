package com.aknaf.utbk_snbt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> chatList;

    // Konstruktor untuk menerima data list chat
    public ChatAdapter(List<ChatMessage> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menghubungkan adapter dengan desain item_chat.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chat = chatList.get(position);

        // LOGIKA PENAMPILAN BALON CHAT
        if (chat.getSender().equals("user")) {
            // Jika pengirimnya "user": Tampilkan balon hijau (kanan), sembunyikan putih (kiri)
            holder.layoutUser.setVisibility(View.VISIBLE);
            holder.layoutBot.setVisibility(View.GONE);
            holder.tvUserMessage.setText(chat.getMessage());
        } else {
            // Jika pengirimnya "bot": Tampilkan balon putih (kiri), sembunyikan hijau (kanan)
            holder.layoutBot.setVisibility(View.VISIBLE);
            holder.layoutUser.setVisibility(View.GONE);
            holder.tvBotMessage.setText(chat.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    // Class ViewHolder untuk mendeklarasikan ID dari item_chat.xml
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutBot, layoutUser;
        TextView tvBotMessage, tvUserMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutBot = itemView.findViewById(R.id.layoutBot);
            layoutUser = itemView.findViewById(R.id.layoutUser);
            tvBotMessage = itemView.findViewById(R.id.tvBotMessage);
            tvUserMessage = itemView.findViewById(R.id.tvUserMessage);
        }
    }
}