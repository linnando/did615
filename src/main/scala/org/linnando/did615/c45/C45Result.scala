package org.linnando.did615.c45

import org.linnando.did615.backprop.{Example, Income}

case class C45Result(tree: TreeNode[Example],
                     testSet: Seq[(Example, Income.Value)])
