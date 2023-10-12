package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEditText, contentEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    String title,content,docId;
    boolean isEditmode = false;
    TextView deleteNoteTextViewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn);

        //se reciben los datos
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if (docId!= null && !docId.isEmpty()) {
            isEditmode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);

        if (isEditmode) {
            pageTitleTextView.setText("Edita tu nota");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }

        saveNoteBtn.setOnClickListener(v-> saveNote());
        deleteNoteTextViewBtn.setOnClickListener(view -> deleteNoteFromFirebase());

    }

    public void saveNote() {
        String noteTittle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        if (noteTittle == null || noteTittle.isEmpty()) {
            titleEditText.setError("Titulo es requerido");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTittle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFireBase(note);
    }

    public void saveNoteToFireBase(Note note) {
        DocumentReference documentReference;
        if (isEditmode) {
            //actualiza la nota
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);

        }else {
            //crea una nueva nota
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //se agrega la nota
                    Utility.showToast(NoteDetailsActivity.this,"Nota agregada satisfactoriamente");
                    finish();
                }else {
                    Utility.showToast(NoteDetailsActivity.this,"Error, no se agrego la nota");
                }
            }
        });
    }

    public void deleteNoteFromFirebase() {
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //se elimina la nota
                    Utility.showToast(NoteDetailsActivity.this,"Nota eliminada satisfactoriamente");
                    finish();
                }else {
                    Utility.showToast(NoteDetailsActivity.this,"Error, no se elimino la nota");
                }
            }
        });
    }

}