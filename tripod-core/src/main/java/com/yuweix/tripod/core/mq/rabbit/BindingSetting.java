package com.yuweix.tripod.core.mq.rabbit;


import java.util.List;


public interface BindingSetting {
    List<Item> getBindings();

    class Item {
        private String queue;
        private String exchange;
        private String routeKey;

        public void setQueue(String queue) {
            this.queue = queue;
        }

        public String getQueue() {
            return queue;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getExchange() {
            return exchange;
        }

        public void setRouteKey(String routeKey) {
            this.routeKey = routeKey;
        }

        public String getRouteKey() {
            return routeKey;
        }
    }
}
