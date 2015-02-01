package at.weini;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main
{

	/*
	 * 
	 * 
	 * http://twitchemotes.com/api_cache/v2/global.json Global Emoticons
	 * 
	 * http://twitchemotes.com/api_cache/v2/subscriber.json Subscriber Emoticons
	 * 
	 * http://twitchemotes.com/api_cache/v2/sets.json Emote Set Mapping
	 * 
	 * http://twitchemotes.com/api_cache/v2/images.json Image ID Mapping
	 */

	public static void main(String[] args)
	{
		File dir = new File("/home/weini/.purple/smileys/subscriberTwitchSmileys/");
		System.out.println("Checking if path exists " + dir.getPath());
		System.out.println("Creating directory " + dir.getPath());
		System.out.println(dir.mkdirs());

		try
		{
			FileWriter writer = new FileWriter(new File(dir.getPath() + File.separator + "theme"));

			writer.write("Name=Subscriber Twitch.tv Emotes\n");
			writer.write("Description=All Subscriber Emotes from Twitch.tv\n");
			writer.write("\n[default]\n");

			GlobalDownload(writer, dir);
			SubscriberDownload(writer, dir);

			writer.close();
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}

	public static void SubscriberDownload(FileWriter writer, File dir)
	{
		String s = "";
		try
		{
			s = FileDownloader.downloadFile("http://twitchemotes.com/api_cache/v2/subscriber.json");
		} catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		JSONObject o = new JSONObject(s);

		String imageiconURL = "http:" + o.getJSONObject("template").getString("small");

		JSONObject channels = o.getJSONObject("channels");

		int counter = 1;

		for (String channelname : channels.keySet())
		{
			System.out.println(channelname);
			JSONArray smileys = channels.getJSONObject(channelname).getJSONArray("emotes");
			for (int j = 0; j < smileys.length(); j++)
			{
				JSONObject emote = (JSONObject) smileys.get(j);
				try
				{
					System.out.println(counter + "/" + channels.length() + "\t" + emote.getString("code") + "\t" + imageiconURL.replace("{image_id}", "" + emote.getInt("image_id")));
					if (!new File(dir.getPath() + File.separator + emote.getString("code")).exists())
					{
						FileDownloader.saveImage(imageiconURL.replace("{image_id}", "" + emote.getInt("image_id")), dir.getPath() + File.separator + emote.getString("code"));

					}
					writer.write(emote.getString("code") + "\t\t\t\t" + emote.getString("code") + "\n");
				} catch (Exception e)
				{
					System.err.println("Can't download " + emote.getString("code"));
				}
			}
			counter++;
		}
	}

	public static void GlobalDownload(FileWriter writer, File dir)
	{
		String s = "";
		try
		{
			s = FileDownloader.downloadFile("http://twitchemotes.com/api_cache/v2/global.json");
		} catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		JSONObject o = new JSONObject(s);

		String imageiconURL = "http:" + o.getJSONObject("template").getString("small");

		JSONObject channels = o.getJSONObject("emotes");

		int counter = 1;

		for (String channelname : channels.keySet())
		{
			try
			{
				JSONObject emote = channels.getJSONObject(channelname);
				System.out.println(counter + "/" + channels.length() + "\t" + channelname + "\t" + imageiconURL.replace("{image_id}", "" + emote.getInt("image_id")));
				if (!new File(dir.getPath() + File.separator + channelname).exists())
				{
					FileDownloader.saveImage(imageiconURL.replace("{image_id}", "" + emote.getInt("image_id")), dir.getPath() + File.separator + channelname);
				}
				writer.write(channelname + "\t\t\t\t" + channelname + "\n");
			} catch (Exception e)
			{
				e.printStackTrace();
				System.err.println("Can't download " + channelname);
			}
			counter++;
		}
	}
}
