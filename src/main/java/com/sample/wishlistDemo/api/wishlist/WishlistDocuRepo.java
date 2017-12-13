package com.sample.wishlistDemo.api.wishlist;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sample.wishlistDemo.api.generated.Wishlist;
import com.sample.wishlistDemo.api.generated.WishlistItem;

@Component
public class WishlistDocuRepo {

	@Autowired
	private WishlistServiceWrapper wishlistService;

	public List<Wishlist> get() {
		try {
			Wishlist[] wishlists = wishlistService.get();
			return Arrays.asList(wishlists);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean post(final Wishlist wishlist) {
		try {
			wishlistService.post(wishlist);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public Optional<Wishlist> get(final String id) {
		try {
			return wishlistService.get(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public boolean put(final String id, final Wishlist wishlist) {
		try {
			wishlistService.put(id, wishlist);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean delete(final String id) {
		try {
			wishlistService.delete(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<WishlistItem> getItems(final String id) {
		try {
			WishlistItem[] wishlistItems = wishlistService.getItems(id);
			return Arrays.asList(wishlistItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean postItems(final String id, final WishlistItem item) {
		try {
			wishlistService.postItem(id, item);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}