<script lang="ts" setup>
import { ref, type Ref } from 'vue'
import { userInputStore } from '@/stores/inputState'
import { useRightClickStore } from '@/stores/rightClick'
import sideMenuMethod from '@/utils/sideMenuMethod'


interface listItem {
    name: string
    href: string
    icon?: any
    current: boolean
    initial?: string
}

interface Props {
    navigation: Ref<listItem[]>
    group?: listItem[]
}

const props = defineProps<Props>()

// ==================== store ====================
const inputStore = userInputStore()
const rightClickStore = useRightClickStore()
// ==================== store ====================





const emit = defineEmits<{
    (event: "right-click", item: listItem, MouseEvent: MouseEvent): void;
}>();



const editInputValue = ref('')
const currentValue = ref<listItem | null>(null)

const handleRightClick = (item: listItem, event: MouseEvent) => {
    // console.log(item);


    editInputValue.value = item.name
    currentValue.value = item

    // 输出item数据
    // console.log(item);

    // 把event鼠标事件以及item数据项传递出去
    emit("right-click", item, event); // 将事件传递给父组件

};

const inputError = ref(false)

// 这是解决enter键和blur事件的冲突问题
const handleEnterKey = () => {
    sideMenuMethod.handleKeyup()
    saveEdit();
}

const handleBlur = () => {
    if (!sideMenuMethod.isEnterPressed.value) {
        saveEdit();
    }
    sideMenuMethod.handleBlur()
}

const saveEdit = () => {
    // 如果输入框为空，显示错误提示
    if (!editInputValue.value.trim()) {
        inputError.value = true
        return
    }
    // 重置错误提示
    inputError.value = false
    sideMenuMethod.saveEditData(currentValue.value, editInputValue.value)
}



</script>

<template>


    <ul role="list" class="-mx-2 space-y-1">
        <li v-for="item in props.navigation.value" :key="item.name" @contextmenu.right="handleRightClick(item, $event)">

            <a :href="item.href" @contextmenu.right="handleRightClick(item, $event)" @dragstart.prevent
                :class="[item.current ? 'bg-gray-800 text-white' : 'text-gray-400 hover:bg-gray-800 hover:text-white', 'group flex gap-x-3 rounded-md p-2 text-sm/6 font-semibold']">
                <component :is="item.icon" class="size-6 shrink-0" aria-hidden="true" />

                <div :class="[inputError ? 'input-error tooltip' : '']" data-tip="请输入数据">
                    <input type="text" v-model="editInputValue"
                        v-if="inputStore.isEditingActive && rightClickStore.selectItem === item.name"
                        :class="[inputError ? 'input-error' : '', 'input input-bordered input-sm w-4/5 text-black']"
                        @blur="handleBlur" @keyup.enter="handleEnterKey()" v-focus="true" />


                    <span v-else>{{ item.name }}</span>
                </div>
            </a>

        </li>
    </ul>


    <!-- <li v-if="props.group && props.group.length > 0">
        <div class="text-xs/6 font-semibold text-gray-400">Your teams</div>
        <ul role="list" class="-mx-2 mt-2 space-y-1">
            <li v-for="item in props.group" :key="item.name">
                <a :href="item.href" @contextmenu.right="handleRightClick(item, $event)"
                    :class="[item.current ? 'bg-gray-800 text-white' : 'text-gray-400 hover:bg-gray-800 hover:text-white', 'group flex gap-x-3 rounded-md p-2 text-sm/6 font-semibold']">
                    <span v-if="item.initial"
                        class="flex size-6 shrink-0 items-center justify-center rounded-lg border border-gray-700 bg-gray-800 text-[0.625rem] font-medium text-gray-400 group-hover:text-white">{{
                            item.initial }}</span>
                    <span class="truncate">{{ item.name }}</span>
                </a>
            </li>
        </ul>
    </li> -->


</template>