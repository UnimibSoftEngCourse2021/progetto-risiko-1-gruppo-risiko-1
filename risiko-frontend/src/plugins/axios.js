import { vueApp } from "@/main.js";
import axios from "axios";

const config = {
    baseURL: process.env.baseURL
};

const _axios = axios.create(config);

_axios.interceptors.request.use(
    config => {
        vueApp.$store.commit("setLoading", true);
        return config;
    },
    error => {
        vueApp.$store.commit("setLoading", false);
        vueApp.$store.commit("setError", true);
        return Promise.reject(error);
    }
);

// Add a response interceptor
_axios.interceptors.response.use(
    response => {
        vueApp.$store.commit("setLoading", false);
        return response;
    },
    error => {
        vueApp.$store.commit("setLoading", false);
        vueApp.$store.commit("setError", true);
        return Promise.reject(error);
    }
);

export default _axios;
