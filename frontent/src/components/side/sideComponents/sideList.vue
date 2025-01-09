<script lang="ts" setup>

interface listItem {
    name: string
    href: string
    icon?: any
    current: boolean
    initial?: string
    onclick: (event: MouseEvent) => void
}

interface Props {
    navigation: listItem[]
    group?: listItem[]
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (event: "right-click", MouseEvent: MouseEvent): void;
}>();

const handleRightClick = (item: listItem, MouseEvent: MouseEvent) => {
    // item用来根据点击的具体数据项调整菜单内容、执行操作或记录日志
  emit("right-click", MouseEvent); // 将事件传递给父组件
};

</script>

<template>

    <li>
        <ul role="list" class="-mx-2 space-y-1">
            <li v-for="item in props.navigation" :key="item.name">
                <a :href="item.href" @contextmenu.right="handleRightClick(item,$event)"
                    :class="[item.current ? 'bg-gray-800 text-white' : 'text-gray-400 hover:bg-gray-800 hover:text-white', 'group flex gap-x-3 rounded-md p-2 text-sm/6 font-semibold']">
                    <component :is="item.icon" class="size-6 shrink-0" aria-hidden="true" />
                    {{ item.name }}
                </a>
            </li>
        </ul>
    </li>

    <li v-if="props.group && props.group.length > 0">
        <div class="text-xs/6 font-semibold text-gray-400">Your teams</div>
        <ul role="list" class="-mx-2 mt-2 space-y-1">
            <li v-for="item in props.group" :key="item.name">
                <a :href="item.href" @contextmenu.right="handleRightClick(item,$event)"
                    :class="[item.current ? 'bg-gray-800 text-white' : 'text-gray-400 hover:bg-gray-800 hover:text-white', 'group flex gap-x-3 rounded-md p-2 text-sm/6 font-semibold']">
                    <span v-if="item.initial"
                        class="flex size-6 shrink-0 items-center justify-center rounded-lg border border-gray-700 bg-gray-800 text-[0.625rem] font-medium text-gray-400 group-hover:text-white">{{
                            item.initial }}</span>
                    <span class="truncate">{{ item.name }}</span>
                </a>
            </li>
        </ul>
    </li>


</template>