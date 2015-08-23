package com.betahikaru.learn.twitter.main;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Scanner;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterMain {

	private static String CONSUMER_KEY = null, CONSUMER_SECRET = null, ACCESS_KEY_FORAPP = null,
			ACCESS_SECRET_FORAPP = null;
	private static boolean DEBUG = false;

	public static void main(String[] args) {
		TwitterMain main = new TwitterMain();

		/// load properties
		Properties properties = new Properties();
		try {
			properties.load(TwitterMain.class.getClassLoader().getResourceAsStream("twitter4j.properties"));
			DEBUG = Boolean.parseBoolean(properties.getProperty("debug"));
			CONSUMER_KEY = properties.getProperty("oauth.consumerKey");
			CONSUMER_SECRET = properties.getProperty("oauth.consumerSecret");
			ACCESS_KEY_FORAPP = properties.getProperty("oauth.accessToken");
			ACCESS_SECRET_FORAPP = properties.getProperty("oauth.accessTokenSecret");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		/// Get OAuth Access Token
		main.openAuthUrlForUserAccount();
		sleep();

		/// Other sample code , required Access Key and Secret for Twitter App
		main.outputUserStats();
		sleep();

		/// Other sample code
		main.outputOAuth2TokenForApplicationAccount();
		sleep();
	}

	private static void sleep() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * https://dev.twitter.com/web/sign-in/implementing
	 * 
	 * Other Links -
	 * https://blog.twitter.com/2014/bootstrapping-sign-in-with-twitter -
	 * https://twittercommunity.com/t/invalid-oauth-verifier-parameter/38724
	 */
	private void openAuthUrlForUserAccount() {
		System.out.println("****" + Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			ConfigurationBuilder confBuilder = new ConfigurationBuilder();
			confBuilder.setDebugEnabled(DEBUG).setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET)
					.setOAuthAccessToken(null).setOAuthAccessTokenSecret(null);
			Twitter twitter = new TwitterFactory(confBuilder.build()).getInstance();

			try {
				/// https://dev.twitter.com/web/sign-in/implementing :Step1
				RequestToken requestToken = twitter.getOAuthRequestToken();
				System.out.println("Request token        : " + requestToken.getToken());
				System.out.println("Request token secret : " + requestToken.getTokenSecret());

				/// https://dev.twitter.com/web/sign-in/implementing :Step2
				String url = null;
				url = requestToken.getAuthenticationURL();
				System.out.println("Authentication URL   : " + url);

				// Open Authentication url
				Desktop.getDesktop().browse(new URI(url));

				/// Twitter redirects following url
				// :success:
				// => GET /?callback&oauth_token=xxx&oauth_verifier=xxx HTTP/1.1
				// :fail:
				// => GET /?callback&denied=xxx HTTP/1.1

				/// https://dev.twitter.com/web/sign-in/implementing :Step3
				String oauthVerifier = null;
				if (oauthVerifier == null || "".equals(oauthVerifier)) {
					Scanner scanner = new Scanner(System.in);
					System.out.print("Input 'oauth_verifier' value => ");
					oauthVerifier = scanner.nextLine();
					scanner.close();
				}
				AccessToken accessToken = twitter.getOAuthAccessToken(oauthVerifier);
				System.out.println("OAuth AccessToken        : " + accessToken.toString());
				System.out.println("OAuth AccessToken Name   : " + accessToken.getScreenName());
				System.out.println("OAuth AccessToken UserId : " + accessToken.getUserId());
				System.out.println("OAuth AccessToken Token  : " + accessToken.getToken());
				System.out.println("OAuth AccessToken Secret : " + accessToken.getTokenSecret());
			} catch (IllegalStateException ie) {
				if (!twitter.getAuthorization().isEnabled()) {
					System.out.println("OAuth consumer key/secret is not set.");
				} else {
					ie.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * http://kikutaro777.hatenablog.com/entry/2013/07/19/195626
	 */
	private void outputUserStats() {
		System.out.println("****" + Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			ConfigurationBuilder confBuilder = new ConfigurationBuilder();
			confBuilder.setDebugEnabled(DEBUG).setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET)
					.setOAuthAccessToken(ACCESS_KEY_FORAPP).setOAuthAccessTokenSecret(ACCESS_SECRET_FORAPP);
			Twitter twitter = new TwitterFactory(confBuilder.build()).getInstance();

			User user = twitter.verifyCredentials();
			System.out.println("User Name         ：" + user.getName());
			System.out.println("User Display Name ：" + user.getScreenName());
			System.out.println("Following Count   ：" + user.getFriendsCount());
			System.out.println("Follower Count    ：" + user.getFollowersCount());
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * http://workpiles.com/2014/03/android-twitter4j-asynctwitter/
	 */
	private void outputOAuth2TokenForApplicationAccount() {
		System.out.println("****" + Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			ConfigurationBuilder confBuilder = new ConfigurationBuilder();
			confBuilder.setDebugEnabled(DEBUG).setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET)
					.setOAuthAccessToken(ACCESS_KEY_FORAPP).setOAuthAccessTokenSecret(ACCESS_SECRET_FORAPP)
					.setApplicationOnlyAuthEnabled(true);
			Twitter twitter = new TwitterFactory(confBuilder.build()).getInstance();

			System.out.println("Access Token      :" + twitter.getOAuth2Token().getAccessToken());
		} catch (TwitterException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

}
