<template>
  <div class="dropdown dropdown-hover dropdown-bottom">
    <label
      tabindex="0"
      :class="['btn btn-sm btn-ghost', isActive ? 'bg-base-300' : '']"
      :title="config.title"
    >
      <component :is="config.icon" class="h-4 w-4" />
    </label>

    <ul
      tabindex="0"
      class="dropdown-content p-2 shadow bg-base-100 rounded-box text-sm overflow-auto w-36 h-64"
    >
      <li v-for="(option, index) in config.options" :key="index">
        <template v-if="option.value === 'divider'">
          <hr class="my-1 border-t border-gray-200" />
        </template>
        <template v-else>
          <a
            @click.stop.prevent="handleSelect(option.value)"
            :class="[
              'px-3 py-2 flex items-center rounded-sm transition-colors',
              config.isActive?.(option.value) ? 'bg-blue-50 text-blue-600' : 'hover:bg-gray-100',
            ]"
          >


            <component
              v-if="typeof option.label !== 'string' && option.label.type === 'icon'"
              :is="option.label.name"
              class="h-4 w-4 mr-2"
            />
            <span v-else :style="{ fontFamily: option.value }" class="text-sm group-hover:text-primary">
              {{ option.label }}
            </span>
          </a>
        </template>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import type { DropdownItem } from '@/views/main/types/mainTypes'

const props = defineProps<{
  config: DropdownItem
  isActive: boolean
}>()

const handleSelect = (value: string) => {
  props.config.action(value)
}
</script>
