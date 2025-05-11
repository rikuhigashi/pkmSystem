import './assets/main.css'
import './assets/css/fontFamily.css'


import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import registerIcons from './plugins/autoRegisterIcons'
import vFocus from './directives/v-focus'
import AppAlert from "@/components/appAlert.vue";


const app = createApp(App)

registerIcons(app)
app.directive('focus', vFocus)
app.component('AppAlert',AppAlert)

app.use(createPinia())
app.use(router)

app.mount('#app')

