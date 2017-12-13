package com.sample.wishlistDemo.api.wishlist;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.sample.wishlistDemo.api.generated.Wishlist;
import com.sample.wishlistDemo.api.generated.WishlistItem;
import com.sample.wishlistDemo.api.utils.JsonUtils;
import com.sample.wishlistDemo.api.exceptions.CallingYaaSServiceException;

@Component
@PropertySource("classpath:default.properties")
public class WishlistServiceWrapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(WishlistServiceWrapper.class);

	@Value("${docuRepoURL}")
	private String URI;
	@Value("${projectIDAkaTenant}")
	private String tenant;
	@Value("${yaaSClientsIdentifier}")
	private String client;
	@Value("${wishlistType}")
	private String wishlistType;
	@Value("${wishlistItemType}")
	private String wishlistItemType;
	@Value("${wishlistTag}")
	private String tagName;

	@Autowired
	private OAuthWrapper oaw;

	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private String accessToken;
	private long tokenAcquireAt;
	private long tokenExpiresIn;

	public WishlistServiceWrapper() {
	}

	public Wishlist[] get() {
		try {
			String token = getToken();
			String url = URI + "/" + tenant + "/" + client + "/data/" + wishlistType + "?fetchAll=true";
			LOGGER.debug("get url is [{}]", url);
			HttpEntity<String> request = new HttpEntity<>(headerWithToken(token));
			Wishlist[] wishlists = new RestTemplate().exchange(url, HttpMethod.GET, request, Wishlist[].class)
					.getBody();
			return wishlists;
		} catch (RestClientException e) {
			handleRestClientException(e);
			e.printStackTrace();
		}
		throw new CallingYaaSServiceException();
	}

	public void post(final Wishlist wishlist) {
		try {
			// The DocumentService has limitation of simultaneously created indexes,
			// so the client should always generate id itself
			wishlist.setId(generateId());
			wishlist.setCreatedAt(new Date());

			String token = getToken();
			String url = URI + "/" + tenant + "/" + client + "/data/" + wishlistType;
			LOGGER.debug("post url is [{}]", url);

			HttpEntity<String> request = new HttpEntity<>(JsonUtils.toJson(wishlist), headerWithToken(token));
			Map<String, String> map = new RestTemplate().postForObject(url, request, Map.class);
			if (map != null && map.containsKey("id")) {
				// Always update id
				wishlist.setId(map.get("id"));
				if (map.containsKey("link")) {
					try {
						// FIXME: Seems it should not use the link from Document
						// NOTE: Actually the URL field was never persisted
						wishlist.setUrl(new URI(map.get("link")));
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}

				return;
			}
		} catch (RestClientException e) {
			handleRestClientException(e);
			e.printStackTrace();
		}
		throw new CallingYaaSServiceException();
	}

	public Optional<Wishlist> get(final String id) {
		try {
			String token = getToken();
			String url = getUrlByTypeAndId(wishlistType, id);
			LOGGER.debug("get url is [{}]", url);
			HttpEntity<String> request = new HttpEntity<>(headerWithToken(token));
			Wishlist wishlist = new RestTemplate().exchange(url, HttpMethod.GET, request, Wishlist.class).getBody();
			return Optional.of(wishlist);
		} catch (RestClientException e) {
			handleRestClientException(e);
			e.printStackTrace();
		}
		throw new CallingYaaSServiceException();
	}

	public void put(final String id, final Wishlist wishlist) {
		try {
			// Always update id
			wishlist.setId(id);

			String token = getToken();
			String url = getUrlByTypeAndId(wishlistType, id);
			LOGGER.debug("put url is [{}]", url);
			HttpEntity<String> request = new HttpEntity<>(JsonUtils.toJson(wishlist), headerWithToken(token));
			new RestTemplate().put(url, request);
			return;
		} catch (RestClientException e) {
			handleRestClientException(e);
			e.printStackTrace();
		}
		throw new CallingYaaSServiceException();
	}

	public void delete(final String id) {
		try {
			String token = getToken();
			String url = getUrlByTypeAndId(wishlistType, id);
			LOGGER.debug("delete url is [{}]", url);
			HttpEntity<String> request = new HttpEntity<>(headerWithToken(token));
			Object o = new RestTemplate().exchange(url, HttpMethod.DELETE, request, Object.class).getBody();
			// Remove wishlist items and tags related to this wishlist
			deleteItems(id);
			deleteTags(id);
			return;
		} catch (RestClientException e) {
			handleRestClientException(e);
			e.printStackTrace();
		}
		throw new CallingYaaSServiceException();
	}

	public WishlistItem[] getItems(final String id) {
		try {
			String token = getToken();
			HttpEntity<String> request = new HttpEntity<>(headerWithToken(token));
			String url = URI + "/" + tenant + "/" + client + "/data/" + wishlistItemType;
			url += "?fetchAll=true&q=" + tagName + ":all(" + id + ")";
			LOGGER.debug("get url is [{}]", url);
			WishlistItem[] wishlistItems = new RestTemplate()
					.exchange(url, HttpMethod.GET, request, WishlistItem[].class).getBody();
			return wishlistItems;
		} catch (RestClientException e) {
			handleRestClientException(e);
			e.printStackTrace();
		}
		throw new CallingYaaSServiceException();
	}

	public void postItem(final String id, final WishlistItem item) {
		try {
			// The DocumentService has limitation of simultaneously created indexes,
			// so the client should always generate id itself
			item.setId(generateId());
			item.setCreatedAt(new Date());

			String token = getToken();
			String url = URI + "/" + tenant + "/" + client + "/data/" + wishlistItemType;
			LOGGER.debug("post url is [{}]", url);
			HttpEntity<String> request = new HttpEntity<>(JsonUtils.toJson(item), headerWithToken(token));
			Map<String, String> map = new RestTemplate().postForObject(url, request, Map.class);
			if (map != null && map.containsKey("id")) {
				// Add tag for this item to refer to the wishlist
				String itemId = map.get("id");
				if (addItemTag(itemId, id)) {
					return;
				} else {
					// Add tag failed, remove previous added wishlistItem
					url = getUrlByTypeAndId(wishlistItemType, itemId);
					LOGGER.debug("delete url is [{}]", url);
					request = new HttpEntity<>(headerWithToken(token));
					Object o = new RestTemplate().exchange(url, HttpMethod.DELETE, request, Object.class).getBody();
				}
			}
		} catch (RestClientException e) {
			handleRestClientException(e);
			e.printStackTrace();
		}
		throw new CallingYaaSServiceException();
	}

	private void deleteItems(String tag) {
		try {
			String token = getToken();
			String url = URI + "/" + tenant + "/" + client + "/data/" + wishlistItemType;
			url += "?q=" + tagName + ":all(" + tag + ")";
			LOGGER.debug("post url is [{}]", url);
			HttpEntity<String> request = new HttpEntity<>(headerWithToken(token));
			Map<String, String>[] maps = new RestTemplate().exchange(url, HttpMethod.GET, request, Map[].class)
					.getBody();
			if (maps != null) {
				for (Map<String, String> map : maps) {
					if (map.containsKey("id")) {
						String itemId = map.get("id");
						url = getUrlByTypeAndId(wishlistItemType, itemId);
						LOGGER.debug("delete url is [{}]", url);
						request = new HttpEntity<>(headerWithToken(token));
						Object o = new RestTemplate().exchange(url, HttpMethod.DELETE, request, Object.class).getBody();
					}
				}
			}
		} catch (RestClientException e) {
			handleRestClientException(e);
			e.printStackTrace();
		}
	}

	private boolean addItemTag(String itemId, String tag) {
		try {
			String token = getToken();
			String url = URI + "/" + tenant + "/" + client + "/tags/" + wishlistItemType + "/" + itemId + "/" + tagName
					+ "?tags=" + tag;
			LOGGER.debug("post url is [{}]", url);
			HttpEntity<String> request = new HttpEntity<>(headerWithToken(token));
			Map<String, String> map = new RestTemplate().postForObject(url, request, Map.class);
			if (map != null && map.containsKey("status")) {
				String status = map.get("status");
				if (status.equals("200")) {
					return true;
				}
			}
		} catch (RestClientException e) {
			handleRestClientException(e);
			e.printStackTrace();
		}
		return false;
	}

	private void deleteTags(String tag) {
		try {
			// Delete all tags related to the tag
			String token = getToken();
			String url = URI + "/" + tenant + "/" + client + "/tags/" + wishlistItemType;
			url += "?removeEmpty=false&tags=" + tag;
			LOGGER.debug("delete url is [{}]", url);
			HttpEntity<String> request = new HttpEntity<>(headerWithToken(token));
			Object o = new RestTemplate().exchange(url, HttpMethod.DELETE, request, Object.class).getBody();
		} catch (RestClientException e) {
			handleRestClientException(e);
			e.printStackTrace();
		}
	}

	private String getUrlByTypeAndId(String type, String id) {
		return URI + "/" + tenant + "/" + client + "/data/" + type + "/" + id;
	}

	private HttpHeaders headerWithToken(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json");
		headers.add("Authorization", "Bearer " + token);
		return headers;
	}

	private String getToken() {
		lock.readLock().lock();

		long curTime = System.currentTimeMillis();
		if (accessToken != null && curTime - tokenAcquireAt < tokenExpiresIn) {
			// Use the existent access token
			lock.readLock().unlock();
			return accessToken;
		}

		// Need acquire token since token non-exists or expired
		String token = accessToken;
		lock.readLock().unlock();

		return acquireTokenFromServer(token);
	}

	private String acquireTokenFromServer(String oldToken) {
		String token = null;

		lock.writeLock().lock();

		try {
			long curTime = System.currentTimeMillis();
			if (accessToken != null && !accessToken.equals(oldToken) && curTime - tokenAcquireAt < tokenExpiresIn) {
				// The token may be already acquired by other thread, should not
				// acquire again
				token = accessToken;
			} else {
				// Get token from OAuthWrapper
				Map<String, Object> tokenMap = oaw.getToken().orElse(null);
				if (tokenMap != null) {
					accessToken = (String) tokenMap.get("access_token");
					Integer expiresIn = (Integer) tokenMap.get("expires_in");
					tokenExpiresIn = expiresIn.longValue() * 1000; // second to millisecond
					tokenAcquireAt = curTime;
					token = accessToken;
				}
			}
		} finally {
			lock.writeLock().unlock();
		}

		if (token == null) {
			throw new CallingYaaSServiceException();
		}

		return token;
	}

	private void handleRestClientException(RestClientException e) {
		if (e instanceof HttpStatusCodeException) {
			if (((HttpStatusCodeException) e).getStatusCode() == HttpStatus.UNAUTHORIZED) {
				// The token has been revoked, reset the token in this case
				lock.writeLock().lock();
				accessToken = null;
				lock.writeLock().unlock();
			}
		}
	}
	
	private String generateId() {
		return UUID.randomUUID().toString().trim().replaceAll("-", "");
	}

}
