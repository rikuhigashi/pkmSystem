import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import registerIcons from './plugins/autoRegisterIcons'

const app = createApp(App)

registerIcons(app)

app.use(createPinia())
app.use(router)

app.mount('#app')
