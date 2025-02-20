package net.iubris.faci.model.world.users;

import java.net.URL;

import com.sleepycat.persist.model.Entity;

@Entity
public class FriendOfFriend extends AbstractFriend {

	private static final long serialVersionUID = -289773176460318021L;

	FriendOfFriend() {}

	public FriendOfFriend(String uid, String name, int friendsCount, int mutualFriendsCount, URL picSmall,
			URL profileURL, User.Sex sex, String significantOtherId) {
		super(uid, name, friendsCount, mutualFriendsCount, picSmall, profileURL, sex, significantOtherId);
	}

	public FriendOfFriend(String uid) {
		super(uid);
	}
}
