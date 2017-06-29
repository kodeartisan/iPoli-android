package io.ipoli.android.feed.persistence;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import io.ipoli.android.app.persistence.OnDataChangedListener;
import io.ipoli.android.feed.data.Profile;
import io.ipoli.android.feed.data.Post;

/**
 * Created by Venelin Valkov <venelin@curiousily.com>
 * on 6/25/17.
 */
public class FirebaseFeedPersistenceService implements FeedPersistenceService {

    private final FirebaseDatabase database;
    private final Map<ValueEventListener, Query> valueListeners;

    public FirebaseFeedPersistenceService(FirebaseDatabase database) {
        this.database = database;
        valueListeners = new HashMap<>();
    }

    @Override
    public void addPost(Post post) {
        DatabaseReference postsRef = database.getReference("/posts");
        DatabaseReference ref = postsRef.push();
        post.setId(ref.getKey());

        Map<String, Object> update = new HashMap<>();
        update.put("/posts/" + post.getId(), post);
        update.put("/profiles/" + post.getPlayerId() + "/postedQuests/" + post.getQuestId(), true);
        database.getReference().updateChildren(update);
    }

    @Override
    public void createProfile(Profile profile) {
        DatabaseReference profileRef = database.getReference("/profiles/" + profile.getId());
        profileRef.setValue(profile);
    }

    @Override
    public void findProfile(String playerId, OnDataChangedListener<Profile> listener) {
        DatabaseReference profileRef = database.getReference("/profiles/" + playerId);
        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onDataChanged(dataSnapshot.getValue(Profile.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void listenForProfile(String playerId, OnDataChangedListener<Profile> listener) {
        DatabaseReference profileRef = database.getReference("/profiles/" + playerId);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onDataChanged(dataSnapshot.getValue(Profile.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        valueListeners.put(valueEventListener, profileRef);
        profileRef.addValueEventListener(valueEventListener);
    }

    @Override
    public void removeAllListeners() {
        for (ValueEventListener valueEventListener : valueListeners.keySet()) {
            Query query = valueListeners.get(valueEventListener);
            query.removeEventListener(valueEventListener);
        }
        valueListeners.clear();
    }

    @Override
    public void removeLike(Post post, String playerId) {
        Map<String, Object> update = new HashMap<>();
        update.put("/posts/" + post.getId() + "/likes/" + playerId, null);
        database.getReference().updateChildren(update);
    }

    @Override
    public void addLike(Post post, String playerId) {
        Map<String, Object> update = new HashMap<>();
        update.put("/posts/" + post.getId() + "/likes/" + playerId, true);
        database.getReference().updateChildren(update);
    }

    @Override
    public void addPostToPlayer(Post post, String playerId) {
        Map<String, Object> update = new HashMap<>();
        update.put("/posts/" + post.getId() + "/addedBy/" + playerId, true);
        database.getReference().updateChildren(update);
    }

    @Override
    public void unfollow(Profile profile, String playerId) {
        Map<String, Object> update = new HashMap<>();
        update.put("/profiles/" + profile.getId() + "/followers/" + playerId, null);
        update.put("/profiles/" + playerId + "/following/" + profile.getId(), null);
        database.getReference().updateChildren(update);
    }

    @Override
    public void follow(Profile profile, String playerId) {
        Map<String, Object> update = new HashMap<>();
        update.put("/profiles/" + profile.getId() + "/followers/" + playerId, true);
        update.put("/profiles/" + playerId + "/following/" + profile.getId(), true);
        database.getReference().updateChildren(update);
    }
}