package com.syakeapps.twe.jaxrs.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * example.
 * 
 * <pre>
 * {
 *    "content":"{{UserName}} がTwitchで配信をはじめました",
 *    "embeds":[
 *       {
 *           "title":"{{ChannelUrl}}",
 *           "url":"{{ChannelUrl}}",
 *           "color":6570404,
 *           "footer":{
 *               "text":"{{StartedAt}}"
 *           },
 *           "author":{
 *               "name":"{{UserName}} is now streaming"
 *           },
 *           "fields":[
 *               {
 *                   "name":"Playing",
 *                   "value":"{{GameName}}",
 *                   "inline":true
 *               },
 *               {
 *                   "name":"Started at (streamer timezone)",
 *                   "value":"{{StartedAt}}",
 *                   "inline":true
 *               }
 *           ]
 *       }
 *    ]
 * }
 * </pre>
 */
public class DiscordWebhookPayload {

    @JsonProperty("content")
    private String content;

    @JsonProperty("embeds")
    private List<Embed> embeds;

    public DiscordWebhookPayload(String content, List<Embed> embeds) {
        this.content = content;
        this.embeds = embeds;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Embed> getEmbeds() {
        return embeds;
    }

    public void setEmbeds(List<Embed> embeds) {
        this.embeds = embeds;
    }

    public static class Embed {
        @JsonProperty("title")
        private String title;

        @JsonProperty("url")
        private String url;

        @JsonProperty("color")
        private long color;

        @JsonProperty("footer")
        private Footer footer;

        @JsonProperty("author")
        private Author author;

        @JsonProperty("fields")
        private List<Field> fields;

        public Embed(String title, String url, long color, Footer footer, Author author, List<Field> fields) {
            this.title = title;
            this.url = url;
            this.color = color;
            this.footer = footer;
            this.author = author;
            this.fields = fields;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public long getColor() {
            return color;
        }

        public void setColor(long color) {
            this.color = color;
        }

        public Footer getFooter() {
            return footer;
        }

        public void setFooter(Footer footer) {
            this.footer = footer;
        }

        public Author getAuthor() {
            return author;
        }

        public void setAuthor(Author author) {
            this.author = author;
        }

        public List<Field> getFields() {
            return fields;
        }

        public void setFields(List<Field> fields) {
            this.fields = fields;
        }

        public static class Footer {
            @JsonProperty("text")
            private String text;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }

        public static class Author {
            @JsonProperty("name")
            private String name;

            public Author(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class Field {
            @JsonProperty("name")
            private String name;

            @JsonProperty("value")
            private String value;

            @JsonProperty("inline")
            private boolean inline;

            public Field() {
                // NOP
            }

            public Field(String name, String value, boolean inline) {
                this.name = name;
                this.value = value;
                this.inline = inline;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public boolean isInline() {
                return inline;
            }

            public void setInline(boolean inline) {
                this.inline = inline;
            }
        }
    }
}
