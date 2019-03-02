package ca.ualberta.CMPUT3012019T02.alexandria.controller;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java9.util.concurrent.CompletableFuture;


public class AuthenticationController {
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    private static AuthenticationController instance;

    private AuthenticationController(){
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    /**
     * Gets the singleton instance of AuthenticationController
     * @return the AuthenticationController instance
     */
    public static AuthenticationController getInstance() {
        if (instance == null) {
            instance = new AuthenticationController();
        }
        return instance;
    }

    /**
     * Determines and returns if a user is authenticated
     * @return if the user is authenticated
     */
    public boolean isAuthenticated() {
        return auth.getCurrentUser() != null;
    }

    /**
     * Logs the current user in
     * @param username the user's username
     * @param password the user's password
     * @return a CompletableFuture signifying this operation's success/failure
     */
    public CompletableFuture<Void> authenticate(String username, final String password) {
        final CompletableFuture<Void> resultFuture = new CompletableFuture<>();
        database.getReference().child("usernameToEmail").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    auth.signInWithEmailAndPassword((String) dataSnapshot.getValue(),password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                resultFuture.complete(null);
                            }
                            else{
                                resultFuture.completeExceptionally(task.getException());
                            }
                        }
                    });
                }
                else{
                    resultFuture.completeExceptionally(new IllegalArgumentException("Username does not exist"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                resultFuture.completeExceptionally(databaseError.toException());
            }
        });
        return resultFuture;
    }

    /**
     * Signs the current user out
     */
    public void deauthenticate() {
        auth.signOut();
    }

    /**
     * Creates a new user account
     * @param username the user's new username
     * @param email the user's email
     * @param password the user's password
     * @return a CompletableFuture signifying this operation's success/failure
     */
    public CompletableFuture<Void> createUser(final String username, final String email, final String password) {
        final CompletableFuture<Void> resultFuture = new CompletableFuture<>();

            database.getReference().child("usernameToEmail").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> authTask) {
                                if(authTask.isSuccessful()){
                                    database.getReference().child("usernameToEmail").child(username).setValue(email)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> databaseTask) {
                                                    if(databaseTask.isSuccessful()){
                                                        resultFuture.complete(null);
                                                    }
                                                    else{
                                                        database.getReference().child("usernameToEmail").child(username).removeValue();
                                                        resultFuture.completeExceptionally(databaseTask.getException());
                                                    }
                                                }
                                            });
                                }
                                else {
                                    database.getReference().child("usernameToEmail").child(username).removeValue();
                                    resultFuture.completeExceptionally(authTask.getException());
                                }
                            }
                        });
                    }
                    else {
                        resultFuture.completeExceptionally(new IllegalArgumentException("Username already exists"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    resultFuture.completeExceptionally(databaseError.toException());
                }
            });

        return resultFuture;
    }

    /**
     * Gets and return the current user's id
     * @return the current user's id
     */
    public String getMyId() {
        return auth.getUid();
    }
}
