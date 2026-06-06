package com.rin.android.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.rin.android.R
import com.rin.android.data.remote.dto.FeedListItemDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onFeedClick: (Int) -> Unit,
    onWriteClick: () -> Unit,
    onDraftsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val tabs = listOf(
        stringResource(R.string.home_tab_published),
        stringResource(R.string.home_tab_draft),
        stringResource(R.string.home_tab_unlisted),
    )
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rin") },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.home_search))
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onWriteClick) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.nav_editor))
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.AutoMirrored.Filled.Article, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_home)) },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onWriteClick,
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_editor)) },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onDraftsClick,
                    icon = { Icon(Icons.Default.Drafts, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_drafts)) },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onProfileClick,
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_profile)) },
                )
            }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = state.selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = state.selectedTab == index,
                        onClick = { viewModel.onTabChange(index) },
                        text = { Text(title) },
                    )
                }
            }
            if (state.feeds.isEmpty() && !state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.home_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val isAtEnd by remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1 } }
                LaunchedEffect(isAtEnd) { if (isAtEnd && state.hasNext) viewModel.loadMore() }

                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    items(state.feeds, key = { it.id }) { feed ->
                        FeedCard(feed = feed, onClick = { onFeedClick(feed.id) })
                    }
                    if (state.isLoading) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedCard(feed: FeedListItemDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = feed.title ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (feed.summary.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = feed.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (feed.hashtags.isNotEmpty()) {
                        feed.hashtags.take(3).forEach { tag ->
                            SuggestionChip(
                                onClick = {},
                                label = { Text("#${tag.name}", style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.height(24.dp).padding(end = 4.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = feed.createdAt.take(10),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (!feed.avatar.isNullOrBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                AsyncImage(
                    model = feed.avatar,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp).clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}
