package com.crowbar.reportdata.pojo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.*;

public class Article {
    /**
     * 定义新闻来源平台的常数。
     */
    public enum Source {
        /**
         * B站网页版：<a href="https://www.bilibili.com/">https://www.bilibili.com/</a>
         */
        Bilibili,
        /**
         * 今日头条网页版：<a href="https://www.toutiao.com/">https://www.toutiao.com/</a>
         */
        Toutiao,
        /**
         * 知乎网页版：<a href="https://www.zhihu.com/">https://www.zhihu.com/</a>
         */
        Zhihu,
        /**
         * 豆瓣网页版：<a href="https://www.douban.com/explore/">https://www.douban.com/explore/</a>
         */
        Douban,
        Google,
        Weibo,
        Pinterest,
        Youtube,
        Twitter
    }

    /**
     * 文章ID。
     */
    public String articleID = "";
    /**
     * 文章标题。
     */
    public String articleTitle = "";
    /**
     * 文章摘要。
     */
    public String articleSummery = "";
    /**
     * 新闻来源平台，使用{@link Source}中规范定义的常数。
     */
    public String articleSource = "";
    /**
     * 文章的链接地址。
     */
    public String articleURL = "";
    /**
     * 文章在网页中的原始html代码，理论上包含可以看到的所有信息，用于今后提取更多其他信息，例如图片等。
     */
    public String articleHTML = "";
    /**
     * 文章内容文本。
     */
    public String articleContent = "";
    /**
     * 文章长度，如果是文本，计算文字数量；如果是视频，则是已秒计的时长。
     */
    public int articleLength = 0;
    /**
     * 文章分类标签信息。
     */
    public List<String> articleCategory = new ArrayList<>();
    /**
     * 文章发布时间。
     */
    @JSONField (format = "yyyy-MM-dd HH:mm:ss")
    public Date articlePostingTime = new Date();
    /**
     * 文章作者ID。
     */
    public String articleAuthorID = "";
    /**
     * 文章作者名称。
     */
    public String articleAuthorName = "";
    /**
     * 文章浏览数。
     */
    public int articleViewNum = 0;
    /**
     * 文章评论数。
     */
    public int articleCommentNum = 0;
    /**
     * 文章弹幕或者实时评论数。
     */
    public int articleLiveCommentNum = 0;
    /**
     * 文章点赞数。
     */
    public int articleLikeNum = 0;
    /**
     * 文章收藏数。
     */
    public int articleCollectNum = 0;
    /**
     * 文章转发/分享数。
     */
    public int articleShareNum = 0;
    /**
     * 文章付费数。
     */
    public int articlePayNum = 0;
    /**
     * 辅助其他信息
     */
    public Map<String, String> others = new HashMap<>();
    /**
     * 文章发布时间。
     */
    @JSONField (format = "yyyy-MM-dd HH:mm:ss")
    public Date articleExposedTime = new Date(0);


    @Override
    public String toString() {
        return articleAuthorName + "{" + articleTitle + "}@" + articlePostingTime;
    }
}
