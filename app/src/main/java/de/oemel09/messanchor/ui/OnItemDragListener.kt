package de.oemel09.messanchor.ui

interface OnItemDragListener {

    fun onItemDismiss(position: Int)
    fun onItemMove(fromPosition: Int, toPosition: Int)
}
