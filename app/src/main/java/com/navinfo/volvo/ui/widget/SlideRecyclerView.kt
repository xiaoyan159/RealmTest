package com.navinfo.volvo.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.*
import android.widget.Scroller
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import java.lang.Math.abs

class SlideRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    //系统最小移动距离
    private val mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

    //最小有效速度
    private val mMinVelocity = 600

    //增加手势控制，双击快速完成侧滑
    private var isDoubleClick = false
    private var mGestureDetector: GestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                e?.let { event ->
                    getSelectItem(event)
                    mItem?.let {
                        val deleteWith = it.getChildAt(it.childCount - 1).width
                        //触发移动至完全展开deleteWidth
                        if (it.scrollX == 0) {
                            mScroller.startScroll(0, 0, deleteWith, 0)
                        } else {
                            mScroller.startScroll(it.scrollX, 0, -it.scrollX, 0)
                        }
                        isDoubleClick = true
                        invalidate()
                        return true
                    }
                }
                //不进行拦截，只作为工具判断下双击
                return false
            }
        })

    //使用速度控制器，增加侧滑速度判定滑动成功，
    //VelocityTracker 由native实现，需要及时释放内存
    private var mVelocityTracker: VelocityTracker? = null

    //流畅滑动
    private var mScroller = Scroller(context)

    //当前选中item
    private var mItem: ViewGroup? = null

    //上次按下的横坐标
    private var mLastX = 0f

    //当前RecyclerView被上层ViewGroup分发到事件，所有事件都会通过dispatchTouchEvent给到
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        mGestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    //viewGroup对子控件的事件拦截，一旦拦截，后续事件序列不会再调用onInterceptTouchEvent
    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        e?.let {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    getSelectItem(e)
                    mLastX = e.x
                }
                MotionEvent.ACTION_MOVE -> {
                    //移动控件
                    return moveItem(e)
                }
//                MotionEvent.ACTION_UP -> {
//                    stopMove(e)
//                }
            }
        }
        return super.onInterceptTouchEvent(e)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent?): Boolean {
        e?.let {
            when (e.action) {
                MotionEvent.ACTION_MOVE -> {
                    moveItem(e)
                    mLastX = e.x
                }
                MotionEvent.ACTION_UP -> {
                    stopMove()
                }
            }
        }
        return super.onTouchEvent(e)
    }

    //活动结束
    //判断一下结束的位置,补充或恢复位置
    private fun stopMove() {
        mItem?.let {
            //如果移动过半，判定左划成功
            val deleteWidth = it.getChildAt(it.childCount - 1).width
            //如果整个移动过程速度大于600，也判定滑动成功
            //注意如果没有拦截ACTION_MOVE,mVelocityTracker是没有初始化的
            var velocity = 0f
            mVelocityTracker?.let { tracker ->
                tracker.computeCurrentVelocity(1000)
                velocity = tracker.xVelocity
            }
            //判断结束情况，移动过半或者向左速度很快都展开
            if ((abs(it.scrollX) >= deleteWidth / 2f) || (velocity < -mMinVelocity)) {
                //触发移动至完全展开
                mScroller.startScroll(it.scrollX, 0, deleteWidth - it.scrollX, 0)
                invalidate()
            } else {
                //如果移动未过半应恢复状态
                mScroller.startScroll(it.scrollX, 0, -it.scrollX, 0)
                invalidate()
            }
        }
        //清除状态
        mLastX = 0f
        //mVeloctityTracker由native实现，需要及时释放
        mVelocityTracker?.apply {
            clear()
            recycle()
        }
        mVelocityTracker = null
    }

    //移动Item
    //绝对值小于删除按钮长度随便移动，大于则不移动
    @SuppressLint("Recycle")
    private fun moveItem(e: MotionEvent): Boolean {
        mItem?.let {
            val dx = mLastX - e.x
            //最小的移动距离应该舍弃，onInterceptTouchEvent不拦截，onTouchEvent内才更新mLastX
//            if (abs(dx) > mTouchSlop) {
                //检查mItem移动后应该在【-deleteLength，0】内
                val deleteWith = it.getChildAt(it.childCount - 1).width
                if ((it.scrollX + dx) <= deleteWith && (it.scrollX + dx) >= 0) {
                    //触发移动
                    it.scrollBy(dx.toInt(), 0)
                    //触发速度计算
                    //这里Rectycle不存在问题，一旦返回true，就会拦截事件，就会到达ACTION_UP去回收
                    mVelocityTracker = mVelocityTracker ?: VelocityTracker.obtain()
                    mVelocityTracker!!.addMovement(e)
                    return true
//                }
            }


        }
        return false
    }

    //获取点击位置
    //通过点击的y坐标除以Item高度得出
    private fun getSelectItem(e: MotionEvent) {
        val frame = Rect()
        mItem = null
        forEach {
            if (it.visibility != GONE) {
                it.getHitRect(frame)
                if (frame.contains(e.x.toInt(), e.y.toInt())) {
                    mItem = it as ViewGroup
                }
            }
        }
    }

    //流畅地滑动
    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mItem?.scrollBy(mScroller.currX, mScroller.currY)
            postInvalidate()
        }
    }
}

