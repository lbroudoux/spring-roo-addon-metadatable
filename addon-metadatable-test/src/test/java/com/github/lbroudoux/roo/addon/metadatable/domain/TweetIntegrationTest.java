package com.github.lbroudoux.roo.addon.metadatable.domain;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.test.RooIntegrationTest;

import com.github.lbroudoux.roo.addon.metadatable.domain.Tweet;
import com.github.lbroudoux.roo.addon.metadatable.domain.TweetDataOnDemand;

@RooIntegrationTest(entity = Tweet.class)
public class TweetIntegrationTest {

   @Autowired
   private TweetDataOnDemand dod;
   
   @Test
   public void testMetadatableAddon() {
      Tweet obj1 = dod.getSpecificTweet(1);
      obj1.addMetadata("key_1", "value_1");
      obj1.addMetadata("key_11", "value_11");
      obj1.merge();
      
      Tweet obj2 = dod.getSpecificTweet(2);
      obj2.addMetadata("key_2", "value_2");
      obj2.addMetadata("key_21", "value_21");
      obj2.merge();
      
      Tweet obj3 = dod.getSpecificTweet(3);
      obj3.addMetadata("key_2", "value_23");
      obj3.addMetadata("key_3", "value_3");
      obj3.merge();
      
      List<Tweet> result = Tweet.findAllTweetsWithMetadata("key_1");
      Assert.assertEquals(1, result.size());
      
      result = Tweet.findAllTweetsWithMetadata("key_2");
      Assert.assertEquals(2, result.size());
      
      result = Tweet.findAllTweetsWithMetadataValue("key_2", "value_2");
      Assert.assertEquals(1, result.size());
      
      obj3.getMetadatas().clear();
      obj3.merge();
      
      result = Tweet.findAllTweetsWithMetadata("key_2");
      Assert.assertEquals(1, result.size());
   }
}
