package com.sample.wishlistDemo.api.generated;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Assert;
import org.junit.Test;

public final class DefaultWishlistsResourceTest extends com.sample.wishlistDemo.api.generated.AbstractResourceTest {
	/**
	 * Server side root resource /wishlists, evaluated with some default
	 * value(s).
	 */
	private static final String ROOT_RESOURCE_PATH = "/wishlists";

	/* get() /wishlists */
	@Test
	public void testGet() {
		final WebTarget target = getRootTarget(ROOT_RESOURCE_PATH).path("");

		final Response response = target.request().get();

		Assert.assertNotNull("Response must not be null", response);
		Assert.assertEquals("Response does not have expected response code", Status.OK.getStatusCode(),
				response.getStatus());
	}

	/* post(entity) /wishlists */
	@Test
	public void testPostWithWishlist() {
		final WebTarget target = getRootTarget(ROOT_RESOURCE_PATH).path("");
		final Wishlist entityBody = new Wishlist();
		final javax.ws.rs.client.Entity<Wishlist> entity = javax.ws.rs.client.Entity.entity(entityBody,
				"application/json");

		final Response response = target.request().post(entity);

		Assert.assertNotNull("Response must not be null", response);
		Assert.assertEquals("Response does not have expected response code", Status.CREATED.getStatusCode(),
				response.getStatus());
	}

	/* get() /wishlists/wishlistId */
	@Test
	public void testGetByWishlistId() {
		WebTarget target = getRootTarget(ROOT_RESOURCE_PATH).path("");

		final Response response = target.request().get();

		if (response.getStatus() == Status.OK.getStatusCode()) {
			Wishlist[] wishlists = response.readEntity(Wishlist[].class);
			for (Wishlist wishlist : wishlists) {
				target = getRootTarget(ROOT_RESOURCE_PATH).path("/" + wishlist.getId());

				final Response newReponse = target.request().get();

				Assert.assertNotNull("Response must not be null", newReponse);
				Assert.assertEquals("Response does not have expected response code", Status.OK.getStatusCode(),
						newReponse.getStatus());
			}
		}

		Assert.assertNotNull("Response must not be null", response);
		Assert.assertEquals("Response does not have expected response code", Status.OK.getStatusCode(),
				response.getStatus());
	}

	/* put(entity) /wishlists/wishlistId */
	@Test
	public void testPutByWishlistIdWithWishlist() {
		WebTarget target = getRootTarget(ROOT_RESOURCE_PATH).path("");
		final Wishlist entityBody = new Wishlist();
		final javax.ws.rs.client.Entity<Wishlist> entity = javax.ws.rs.client.Entity.entity(entityBody,
				"application/json");

		final Response response = target.request().get();

		if (response.getStatus() == Status.OK.getStatusCode()) {
			Wishlist[] wishlists = response.readEntity(Wishlist[].class);
			for (Wishlist wishlist : wishlists) {
				String owner = wishlist.getOwner();
				if (owner != null && !owner.isEmpty()) {
					// Skip wishlist not created by the test
					continue;
				}

				target = getRootTarget(ROOT_RESOURCE_PATH).path("/" + wishlist.getId());

				final Response newReponse = target.request().put(entity);

				Assert.assertNotNull("Response must not be null", newReponse);
				Assert.assertEquals("Response does not have expected response code", Status.OK.getStatusCode(),
						newReponse.getStatus());
			}
		}

		Assert.assertNotNull("Response must not be null", response);
		Assert.assertEquals("Response does not have expected response code", Status.OK.getStatusCode(),
				response.getStatus());
	}

	/* delete() /wishlists/wishlistId */
	@Test
	public void testDeleteByWishlistId() {
		WebTarget target = getRootTarget(ROOT_RESOURCE_PATH).path("");

		final Response response = target.request().get();

		if (response.getStatus() == Status.OK.getStatusCode()) {
			Wishlist[] wishlists = response.readEntity(Wishlist[].class);
			for (Wishlist wishlist : wishlists) {
				String owner = wishlist.getOwner();
				if (owner != null && !owner.isEmpty()) {
					// Skip wishlist not created by the test
					continue;
				}

				target = getRootTarget(ROOT_RESOURCE_PATH).path("/" + wishlist.getId());

				final Response newReponse = target.request().delete();

				Assert.assertNotNull("Response must not be null", newReponse);
				Assert.assertEquals("Response does not have expected response code", Status.NO_CONTENT.getStatusCode(),
						newReponse.getStatus());
			}
		}

		Assert.assertNotNull("Response must not be null", response);
		Assert.assertEquals("Response does not have expected response code", Status.OK.getStatusCode(),
				response.getStatus());
	}

	/* get() /wishlists/wishlistId/wishlistItems */
	@Test
	public void testGetWishlistItems() {
		WebTarget target = getRootTarget(ROOT_RESOURCE_PATH).path("");

		final Response response = target.request().get();

		if (response.getStatus() == Status.OK.getStatusCode()) {
			Wishlist[] wishlists = response.readEntity(Wishlist[].class);
			for (Wishlist wishlist : wishlists) {
				target = getRootTarget(ROOT_RESOURCE_PATH).path("/" + wishlist.getId() + "/wishlistItems");

				final Response newReponse = target.request().get();

				Assert.assertNotNull("Response must not be null", newReponse);
				Assert.assertEquals("Response does not have expected response code", Status.OK.getStatusCode(),
						newReponse.getStatus());
			}
		}

		Assert.assertNotNull("Response must not be null", response);
		Assert.assertEquals("Response does not have expected response code", Status.OK.getStatusCode(),
				response.getStatus());
	}

	/* post(entity) /wishlists/wishlistId/wishlistItems */
	@Test
	public void testPostWithWishlistItem() {
		WebTarget target = getRootTarget(ROOT_RESOURCE_PATH).path("");
		final WishlistItem entityBody = new WishlistItem();
		final javax.ws.rs.client.Entity<WishlistItem> entity = javax.ws.rs.client.Entity.entity(entityBody,
				"application/json");

		final Response response = target.request().get();

		if (response.getStatus() == Status.OK.getStatusCode()) {
			Wishlist[] wishlists = response.readEntity(Wishlist[].class);
			for (Wishlist wishlist : wishlists) {
				String owner = wishlist.getOwner();
				if (owner != null && !owner.isEmpty()) {
					// Skip wishlist not created by the test
					continue;
				}

				target = getRootTarget(ROOT_RESOURCE_PATH).path("/" + wishlist.getId() + "/wishlistItems");

				final Response newResponse = target.request().post(entity);

				Assert.assertNotNull("Response must not be null", newResponse);
				Assert.assertEquals("Response does not have expected response code", Status.CREATED.getStatusCode(),
						newResponse.getStatus());
			}
		}

		Assert.assertNotNull("Response must not be null", response);
		Assert.assertEquals("Response does not have expected response code", Status.OK.getStatusCode(),
				response.getStatus());
	}

	@Override
	protected ResourceConfig configureApplication() {
		final ResourceConfig application = new ResourceConfig();
		application.register(DefaultWishlistsResource.class);
		return application;
	}
}
