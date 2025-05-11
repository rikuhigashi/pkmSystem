<script lang="ts" setup>
import { ref, type Ref } from 'vue'
import { userInputStore } from '@/stores/inputState'
import { useRightClickStore } from '@/stores/rightClick'
import { saveEditData } from '@/views/side/configs/sideMenuMethod'
import type { sideListItem } from '@/views/side/types/sideTypes'
import noCollingBlur from '@/utils/noCollingBlurMethod'

interface Props {
  navigation: Ref<sideListItem[]>
  group?: sideListItem[]
}

const props = defineProps<Props>()

// ==================== store ====================
const inputStore = userInputStore()
const rightClickStore = useRightClickStore()
// ==================== store ====================

const emit = defineEmits<{
  (event: 'right-click', item: sideListItem, MouseEvent: MouseEvent): void
  (event: 'left-click', item: sideListItem, MouseEvent: MouseEvent): void
}>()

const currentValue = ref() //当前值
const editInputValue = ref() //编辑值
const inputError = ref(false)
const originalName = ref('') //存储编辑前的原始名称

const handleRightClick = (item: sideListItem, event: MouseEvent) => {
  currentValue.value = item.name
  editInputValue.value = item
  originalName.value = item.name

  // 输出item数据
  // console.log('item:', item)
  // console.log('currentValue.value:', currentValue.value)
  // console.log('editInputValue.value:', editInputValue.value)

  // 把event鼠标事件以及item数据项传递出去
  emit('right-click', item, event) // 将事件传递给父组件
}

const handleLeftClick = (item: sideListItem, event: MouseEvent) => {
  // leftClickValue.value = item.id
  // console.log('sideListValue:', leftClickValue.value)

  emit('left-click', item, event)
}

// 这是解决enter键和blur事件的冲突问题
const handleEnterKey = () => {
  noCollingBlur.handleKeyup()
  saveEdit()
}

const handleBlur = () => {
  if (!noCollingBlur.isEnterPressed.value) {
    if (currentValue.value === originalName.value) {
      inputError.value = false
      inputStore.closeEditingInput()
      return
    }
    if (currentValue.value !== originalName.value && currentValue.value.trim() !== '') {
      // 有有效修改 → 自动保存
      saveEdit()
    } else {
      currentValue.value = originalName.value
      editInputValue.value.name = originalName.value
      inputError.value = false
      inputStore.closeEditingInput()
    }
  }
  noCollingBlur.handleBlur()
}

const saveEdit = () => {
  // 如果输入框为空，显示错误提示
  if (!currentValue.value?.trim()) {
    inputError.value = true
    currentValue.value = originalName.value // 还原名称
    return
  }
  editInputValue.value = { ...editInputValue.value, name: currentValue.value }

  saveEditData(editInputValue.value.id, editInputValue.value)




  // 重置错误提示
  inputError.value = false
}
</script>

<template>
  <ul role="list" class="-mx-2 space-y-1">
    <li v-for="item in props.navigation.value" :key="item.id">
      <a
        :href="item.href"
        @contextmenu.right="handleRightClick(item, $event)"
        @click="handleLeftClick(item, $event)"
        @dragstart.prevent
        :class="[
          item.current
            ? 'bg-gray-800 text-white ring-2 ring-inset ring-blue-500'
            : 'text-gray-400 hover:bg-gray-800 hover:text-white',
          'group flex gap-x-3 rounded-md p-2 text-sm/6 font-semibold cursor-pointer',
        ]"
      >
        <component :is="item.iconComponent" class="size-6 shrink-0" aria-hidden="true" />

        <div :class="[inputStore.isInputError ? 'input-error tooltip' : '']" data-tip="请输入数据">
          <input
            type="text"
            v-model="currentValue"
            v-if="inputStore.isEditingActive && rightClickStore.selectItemId === item.id"
            :class="[
              inputStore.isInputError ? 'input-error' : '',
              'input input-bordered input-sm w-4/5 text-black',
            ]"
            @blur="handleBlur"
            @keyup.enter="handleEnterKey()"
            v-focus="true"
          />
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
