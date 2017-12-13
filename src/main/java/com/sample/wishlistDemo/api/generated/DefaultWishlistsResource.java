
package com.sample.wishlistDemo.api.generated;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sample.wishlistDemo.api.wishlist.WishlistDocuRepo;

/**
 * Resource class containing the custom logic. Please put your logic here!
 */
@Component("apiWishlistsResource")
@Singleton
public class DefaultWishlistsResource implements WishlistsResource {
	@javax.ws.rs.core.Context
	private javax.ws.rs.core.UriInfo uriInfo;

	@Autowired
	private WishlistDocuRepo wishlistRepo;

	/* GET / */
	@Override
	public Response get(final YaasAwareParameters yaasAware) {
		List<Wishlist> wishlists = wishlistRepo.get();
		if (wishlists != null) {
			return Response.ok().entity(wishlists).build();
		} else {
			return Response.serverError().build();
		}
	}

	/* POST / */
	@Override
	public Response post(final YaasAwareParameters yaasAware, final Wishlist wishlist) {
		if (wishlistRepo.post(wishlist)) {
			URI uri = uriInfo.getAbsolutePath();

			try {
				uri = (new URL(uri.toURL(), wishlist.getId())).toURI();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return Response.created(uri).entity(wishlist).build();
		} else {
			return Response.serverError().build();
		}
	}

	/* GET /{wishlistId} */
	@Override
	public Response getByWishlistId(final YaasAwareParameters yaasAware, final String wishlistId) {
		Optional<Wishlist> wishlist = wishlistRepo.get(wishlistId);
		if (wishlist.isPresent()) {
			return Response.ok().entity(wishlist.get()).build();
		} else {
			return Response.serverError().build();
		}
	}

	/* PUT /{wishlistId} */
	@Override
	public Response putByWishlistId(final YaasAwareParameters yaasAware, final String wishlistId,
			final Wishlist wishlist) {
		if (wishlistRepo.put(wishlistId, wishlist)) {
			return Response.ok().entity(wishlist).build();
		} else {
			return Response.serverError().build();
		}
	}

	/* DELETE /{wishlistId} */
	@Override
	public Response deleteByWishlistId(final YaasAwareParameters yaasAware, final String wishlistId) {
		if (wishlistRepo.delete(wishlistId)) {
			return Response.noContent().build();
		} else {
			return Response.serverError().build();
		}
	}

	/* GET /{wishlistId}/wishlistItems */
	@Override
	public Response getByWishlistIdWishlistItems(final YaasAwareParameters yaasAware, final String wishlistId) {
		List<WishlistItem> items = wishlistRepo.getItems(wishlistId);
		if (items != null) {
			return Response.ok().entity(items).build();
		} else {
			return Response.serverError().build();
		}
	}

	/* POST /{wishlistId}/wishlistItems */
	@Override
	public Response postByWishlistIdWishlistItems(final YaasAwareParameters yaasAware, final String wishlistId,
			final WishlistItem wishlistItem) {
		if (wishlistRepo.postItems(wishlistId, wishlistItem)) {
			return Response.created(uriInfo.getAbsolutePath()).entity(wishlistItem).build();
		} else {
			return Response.serverError().build();
		}
	}

}
