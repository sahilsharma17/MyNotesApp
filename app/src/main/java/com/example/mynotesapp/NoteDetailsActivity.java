package com.example.mynotesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import org.w3c.dom.Text;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEditText,contentEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    String title,content,docId;
    boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title);

        //recieve data
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);

        if(isEditMode){
            pageTitleTextView.setText("Edit Your Note" );
        }

        saveNoteBtn.setOnClickListener( v-> saveNote());
    }

    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        if(noteTitle==null || noteTitle.isEmpty()){
            titleEditText.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);

    }

    void saveNoteToFirebase(Note note){
        DocumentReference docRef;
        if (isEditMode){
            //update the note
            docRef = Utility.getCollectionRefForNotes().document(docId);
        }else{
            //create new note
            docRef = Utility.getCollectionRefForNotes().document();
        }

        docRef.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //note is added
                    Utility.showToast(NoteDetailsActivity.this,"Note added sucessfully");
                    finish();
                }else{
                    //note is not added
                    Utility.showToast(NoteDetailsActivity.this,"Failed while adding note");
                }
            }
        });
    }
}