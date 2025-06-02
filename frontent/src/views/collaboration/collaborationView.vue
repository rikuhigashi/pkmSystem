<script setup lang="ts">
import { useCollaborationStore } from '@/stores/collaborationStore'
import { onMounted } from 'vue'

const collaborationStore = useCollaborationStore()

onMounted(() => {
  console.log(collaborationStore.room, collaborationStore.username)
})
</script>

<template>test</template>

<style scoped></style>
