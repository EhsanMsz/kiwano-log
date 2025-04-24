/*
 * Copyright 2024 Ehsan Msz
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ehsanmsz.kiwanolog.presentation.ui.screen.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ehsanmsz.kiwanolog.R
import com.ehsanmsz.kiwanolog.domain.model.KiwanoHttpModel
import com.ehsanmsz.kiwanolog.presentation.ui.screen.home.component.Log

/**
 * Created by Ehsan Msz on 01 Apr, 2025
 */


/**
 * HomeScreen
 */
@Composable
internal fun HomeScreen(
    navigateToDetail: (Long) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {

    var showDeleteDialog by remember { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDeleteDialog = true },
                content = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Button"
                    )
                }
            )
        }
    ) { paddingValues ->
        HomeContent(
            paddingValues = paddingValues,
            search = viewModel.searchText.collectAsState().value,
            onSearchChange = viewModel::setSearchTextValue,
            onClick = navigateToDetail,
            logs = viewModel.logs.collectAsLazyPagingItems()
        )

        if (showDeleteDialog) {
            Dialog(
                onDismissRequest = { showDeleteDialog = false },
                content = {
                    Surface(
                        modifier = Modifier
                            .wrapContentSize(),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                textAlign = TextAlign.Start,
                                text = stringResource(R.string.kiwano_delete_all_message),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(modifier = Modifier.padding(8.dp))

                            Row {

                                OutlinedButton(onClick = { showDeleteDialog = false }) {
                                    Text(
                                        textAlign = TextAlign.Start,
                                        text = stringResource(R.string.kiwano_cancel),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Spacer(modifier = Modifier.padding(4.dp))

                                Button(
                                    onClick = {
                                        viewModel.removeAll()
                                        showDeleteDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                ) {
                                    Text(
                                        textAlign = TextAlign.Start,
                                        text = stringResource(R.string.kiwano_delete),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    paddingValues: PaddingValues,
    search: String,
    onSearchChange: (String) -> Unit,
    onClick: (id: Long) -> Unit,
    logs: LazyPagingItems<KiwanoHttpModel>
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        AnimatedContent(
            targetState = when {
                logs.loadState.refresh == LoadState.Loading -> LogState.Loading
                logs.itemCount > 0 -> LogState.Loaded
                else -> LogState.NoItem
            },
            transitionSpec = { fadeIn(tween(500)).togetherWith(fadeOut(tween(500))) }
        ) { state ->
            when (state) {
                LogState.Loading -> {

                }

                LogState.Loaded -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(top = 40.dp)
                            .fillMaxSize()
                    ) {
                        item { Spacer(modifier = Modifier.padding(18.dp)) }

                        items(logs.itemCount) { index ->
                            logs[index]?.let { model -> Log(httpModel = model, onClick = onClick) }
                        }
                        item { Spacer(modifier = Modifier.padding(72.dp)) }
                    }
                }

                LogState.NoItem -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.kiwano_no_data),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .padding(top = 16.dp)
                .requiredHeight(56.dp),
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_kiwano_log),
                    contentDescription = null
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .weight(1f)
                ) {
                    if (search.isEmpty()) {
                        Text(
                            text = stringResource(R.string.kiwano_search_hint),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    BasicTextField(
                        modifier = Modifier,
                        value = search,
                        onValueChange = onSearchChange,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.outline),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    )
                }

                AnimatedVisibility(
                    visible = search.isNotEmpty(),
                    enter = scaleIn(tween(200)),
                    exit = scaleOut(tween(200))
                ) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_kiwano_cancel),
                            tint = MaterialTheme.colorScheme.outline,
                            contentDescription = null
                        )
                    }
                }

            }
        }
    }
}
