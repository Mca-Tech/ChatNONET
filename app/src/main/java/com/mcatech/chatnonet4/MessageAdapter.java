package com.mcatech.chatnonet4;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<String> messages;
    private TextToSpeechHelper textToSpeechHelper;
    private StringBuilder currentGeneratingText;

    public MessageAdapter(List<String> messages, Context context) {
        this.messages = messages;
        this.textToSpeechHelper = new TextToSpeechHelper(context);
        this.currentGeneratingText = new StringBuilder();

    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // Method to update the generating message in real-time
    public void updateGeneratingMessage(String word) {
        if (!messages.isEmpty()) {
            currentGeneratingText.append(word);
            messages.set(messages.size() - 1, currentGeneratingText.toString() + "|+|");
            notifyItemChanged(messages.size() - 1);
        }
    }

    // Method to start a new AI message
    public void startNewAiMessage(String text) {
        currentGeneratingText = new StringBuilder();
        messages.add(text); // Start with empty message
        notifyItemInserted(messages.size() - 1);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout textChatDisplayBackgroundId;
        TextView textViewMessage;
        ImageView copyImage;
        ImageView speakImage;
        Boolean updateAIBubble = false;
        Boolean updateUserBubble = false;
        Boolean aiGenerationStatus = false;
        ImageView sourcesButton;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textChatDisplayBackgroundId = itemView.findViewById(R.id.textChatDisplayBackgroundId);
            copyImage = itemView.findViewById(R.id.copy_image);
            speakImage = itemView.findViewById(R.id.speak_image);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            sourcesButton = itemView.findViewById(R.id.sourcesButton);
        }

        public void bind(String message) {
            String displayText = message.replace("|+|", "").replace("|-|", "").replace("|userInput|", "");
            textViewMessage.setText(displayText);

            if (message.contains("|+|") & (!updateAIBubble)) {
                updateAIBubble = true;
                updateUserBubble = false;
                textChatDisplayBackgroundId.setBackgroundColor(Color.TRANSPARENT);
                copyImage.setVisibility(View.GONE);
                speakImage.setVisibility(View.GONE);
                sourcesButton.setVisibility(View.GONE);
                //chatReturnId.setText("ChatNONET");
                //imageView.setImageResource(R.drawable.chatnonet_icon);


            }
            else if (message.contains("|userInput|") & (!updateUserBubble)) {
                updateUserBubble = true;
                updateAIBubble = false;
                textChatDisplayBackgroundId.setBackground(
                        ContextCompat.getDrawable(itemView.getContext(), R.drawable.chatnonet_footer_design)
                );
                //chatReturnId.setText("You");
                //imageView.setImageResource(R.drawable.chatnonet_footer_design);


            }

            else if (message.contains("|-|")){
                copyImage.setVisibility(View.VISIBLE);
                speakImage.setVisibility(View.VISIBLE);
                sourcesButton.setVisibility(View.VISIBLE);
            }

            copyImage.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) v.getContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Message", displayText);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(v.getContext(), "Message copied!", Toast.LENGTH_SHORT).show();
                }
            });

            speakImage.setOnClickListener(v -> {
                if (textToSpeechHelper != null) {
                    textToSpeechHelper.speak(displayText);
                }
            });
        }
    }
}