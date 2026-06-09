/*
 * Copyright (C) 2020-2026 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

import 'package:flutter/material.dart';
import 'package:indoor_map_app/indoor_map_tokens.dart';

/// A text field styled to match the dev-app's HdsSearchBar.
///
/// Provides a leading search icon, an inline clear (×) button.
class AppSearchField extends StatefulWidget {
  const AppSearchField({
    super.key,
    required this.hintText,
    this.onChanged,
    this.onClear,
    this.onCancel,
    this.controller,
    this.focusNode,
    this.autofocus = false,
  });

  final String hintText;

  final ValueChanged<String>? onChanged;
  final VoidCallback? onClear;
  final VoidCallback? onCancel;
  final TextEditingController? controller;
  final FocusNode? focusNode;
  final bool autofocus;

  @override
  State<AppSearchField> createState() => _AppSearchFieldState();
}

class _AppSearchFieldState extends State<AppSearchField> {
  late final TextEditingController _controller;
  late final FocusNode _focusNode;
  bool _hasText = false;
  bool _hasFocus = false;

  @override
  void initState() {
    super.initState();
    _controller = widget.controller ?? TextEditingController();
    _focusNode = widget.focusNode ?? FocusNode();
    _controller.addListener(_onTextChanged);
    _focusNode.addListener(_onFocusChanged);
    _hasText = _controller.text.isNotEmpty;
    _hasFocus = _focusNode.hasFocus;
  }

  void _onTextChanged() {
    final bool nowHasText = _controller.text.isNotEmpty;
    if (_hasText != nowHasText) {
      setState(() => _hasText = nowHasText);
    }
    widget.onChanged?.call(_controller.text);
  }

  void _onFocusChanged() {
    final bool nowHasFocus = _focusNode.hasFocus;
    if (_hasFocus != nowHasFocus) {
      setState(() => _hasFocus = nowHasFocus);
    }
  }

  @override
  void dispose() {
    _focusNode.removeListener(_onFocusChanged);
    if (widget.controller == null) {
      _controller.removeListener(_onTextChanged);
      _controller.dispose();
    }
    if (widget.focusNode == null) {
      _focusNode.dispose();
    }
    super.dispose();
  }

  void _clear() {
    _controller.clear();
    widget.onClear?.call();
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Expanded(
          child: Container(
            height: IndoorMapTokens.size48,
            decoration: BoxDecoration(
              color: Colors.grey.shade100,
              borderRadius: BorderRadius.circular(IndoorMapTokens.radiusCircle),
            ),
            child: TextField(
              controller: _controller,
              focusNode: _focusNode,
              autofocus: widget.autofocus,
              style: const TextStyle(fontSize: IndoorMapTokens.size16),
              decoration: InputDecoration(
                hintText: widget.hintText,
                hintStyle: TextStyle(fontSize: IndoorMapTokens.size16, color: IndoorMapTokens.secondaryTextColor),
                prefixIcon: const Icon(Icons.search, size: IndoorMapTokens.size24),
                suffixIcon: _hasText
                    ? GestureDetector(
                        onTap: _clear,
                        child: const Icon(Icons.clear, size: IndoorMapTokens.size24),
                      )
                    : null,
                border: InputBorder.none,
                isDense: true,
                contentPadding: const EdgeInsets.symmetric(vertical: IndoorMapTokens.size12),
              ),
            ),
          ),
        ),
      ],
    );
  }
}
