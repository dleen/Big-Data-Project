library(ggplot2)
library(reshape2)
library(MASS)

baseline_err = 0.17308

err = c(0.170837,
0.17089466,
0.17091628,
0.17109426)

# err_rel_baseline = err
err_rel = err / err[1]

time=c(5.53, 3.73, 2.47, 2.26)
speedup = time[1] / time


runtime <- data.frame(num_cores=c(1,2,3,4), time=speedup, err=err)

lm_time <- lm(data=runtime, time ~ num_cores)

runtime_facet <- melt(data=runtime, id=c("num_cores"))

height <- 2.25

pdf('./speedup.pdf', width=3, height=height)

theme_set(theme_minimal(base_size = 8, base_family = ""))

p <- ggplot(data=runtime_facet[runtime_facet$variable=="time",], aes(x=num_cores, y=value))
p <- p + geom_point()
p <- p + stat_smooth(method=lm, se=FALSE, fullrange=TRUE)
p <- p + xlab('Number of cores')
p <- p + ylab('Speedup')
p <- p + ggtitle('Hogwild! Speedup')
p <- p + theme(axis.title.y = element_text(vjust=0.2))
p <- p + theme(axis.title.x = element_text(vjust=0.1))
p <- p + theme(title = element_text(vjust=0.8))
p <- p + theme(axis.ticks = element_line(0))
print(p)

dev.off()

pdf('./error.pdf', width=3, height=height)

theme_set(theme_minimal(base_size = 8, base_family = ""))

p <- ggplot(data=runtime_facet[runtime_facet$variable=="err",], aes(x=num_cores, y=value))
p <- p + geom_point()
p <- p + geom_line()
p <- p + xlab('Number of cores')
p <- p + ylab('RMSE')
p <- p + ggtitle('Hogwild! Training error')
p <- p + theme(axis.title.y = element_text(vjust=0.2))
p <- p + theme(axis.title.x = element_text(vjust=0.1))
p <- p + theme(title = element_text(vjust=0.8))
p <- p + theme(axis.ticks = element_line(0))
print(p)

dev.off()