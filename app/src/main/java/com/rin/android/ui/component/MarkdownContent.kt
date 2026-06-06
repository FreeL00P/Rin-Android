package com.rin.android.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.em
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.node.*
import org.commonmark.parser.Parser

private val parser = Parser.builder()
    .extensions(listOf(TablesExtension.create()))
    .build()

@Composable
fun MarkdownContent(markdown: String, modifier: Modifier = Modifier) {
    val styledText = buildAnnotatedString {
        renderNode(parser.parse(markdown), this)
    }
    Text(
        text = styledText,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
    )
}

private fun renderNode(node: Node, builder: AnnotatedString.Builder) {
    var child = node.firstChild
    while (child != null) {
        when (child) {
            is Paragraph -> {
                renderInlineChildren(child, builder)
                builder.append("\n\n")
            }
            is Heading -> {
                val fontSize = when (child.level) {
                    1 -> 2f
                    2 -> 1.5f
                    3 -> 1.25f
                    else -> 1.125f
                }
                builder.pushStyle(SpanStyle(fontSize = fontSize.em, fontWeight = FontWeight.Bold))
                renderInlineChildren(child, builder)
                builder.pop()
                builder.append("\n\n")
            }
            is BlockQuote -> {
                builder.pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                renderNode(child, builder)
                builder.pop()
            }
            is FencedCodeBlock -> {
                builder.pushStyle(SpanStyle(fontFamily = FontFamily.Monospace))
                builder.append(child.literal)
                builder.pop()
                builder.append("\n\n")
            }
            is IndentedCodeBlock -> {
                builder.pushStyle(SpanStyle(fontFamily = FontFamily.Monospace))
                builder.append(child.literal)
                builder.pop()
                builder.append("\n\n")
            }
            is BulletList -> renderNode(child, builder)
            is OrderedList -> renderNode(child, builder)
            is ListItem -> {
                builder.append("• ")
                renderInlineChildren(child, builder)
                builder.append("\n")
            }
            is ThematicBreak -> builder.append("─────────\n\n")
            is SoftLineBreak -> builder.append(" ")
            is HardLineBreak -> builder.append("\n")
            is Emphasis -> {
                builder.pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                renderInlineChildren(child, builder)
                builder.pop()
            }
            is StrongEmphasis -> {
                builder.pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                renderInlineChildren(child, builder)
                builder.pop()
            }
            is Code -> {
                builder.pushStyle(SpanStyle(fontFamily = FontFamily.Monospace))
                builder.append(child.literal)
                builder.pop()
            }
            is Text -> builder.append(child.literal)
            is Link -> {
                renderInlineChildren(child, builder)
            }
            is Image -> builder.append("![${child.title ?: ""}]")
            is HtmlBlock -> {
                builder.append(child.literal)
                builder.append("\n\n")
            }
            else -> renderNode(child, builder)
        }
        child = child.next
    }
}

private fun renderInlineChildren(node: Node, builder: AnnotatedString.Builder) {
    var child = node.firstChild
    while (child != null) {
        renderNode(child, builder)
        child = child.next
    }
}
