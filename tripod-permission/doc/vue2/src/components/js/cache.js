
var keyPre = "xx_";

const sessionCache = {
    set(key, value) {
        if (!sessionStorage) {
            return;
        }
        if (key != null && value != null) {
            sessionStorage.setItem(keyPre + key, value);
        }
    },
    get(key) {
        if (!sessionStorage) {
            return null;
        }
        if (key == null) {
            return null;
        }
        return sessionStorage.getItem(keyPre + key);
    },
    setJSON(key, jsonValue) {
        if (jsonValue != null) {
            this.set(key, JSON.stringify(jsonValue));
        }
    },
    getJSON(key) {
        const value = this.get(key);
        if (value != null) {
            return JSON.parse(value);
        }
    },
    remove(key) {
        sessionStorage.removeItem(keyPre + key);
    }
}

const localCache = {
    set(key, value) {
        if (!localStorage) {
            return;
        }
        if (key != null && value != null) {
            localStorage.setItem(keyPre + key, value);
        }
    },
    get(key) {
        if (!localStorage) {
            return null;
        }
        if (key == null) {
            return null;
        }
        return localStorage.getItem(keyPre + key);
    },
    setJSON(key, jsonValue) {
        if (jsonValue != null) {
            this.set(key, JSON.stringify(jsonValue));
        }
    },
    getJSON(key) {
        const value = this.get(key);
        if (value != null) {
            return JSON.parse(value);
        }
    },
    remove(key) {
        localStorage.removeItem(keyPre + key);
    }
}

export default {
    /**
    * 会话级缓存
    */
    session: sessionCache,
    /**
    * 本地缓存
    */
    local: localCache
}
